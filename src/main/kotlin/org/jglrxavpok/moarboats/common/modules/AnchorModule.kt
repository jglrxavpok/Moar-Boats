package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiAnchorModule
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import org.jglrxavpok.moarboats.common.state.DoubleBoatProperty
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import kotlin.math.sqrt

object AnchorModule: BoatModule(), BlockReason {

    override val id = ResourceLocation(MoarBoats.ModID, "anchor")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc
    override val isMenuInteresting = false
    val spawnPointSet = TranslationTextComponent("gui.anchor.spawnPointSet")

    val activeProperty = BooleanBoatProperty("active")
    val anchorDirectionProperty = IntBoatProperty("anchorDirection")
    val anchorXProperty = DoubleBoatProperty("anchorX")
    val anchorYProperty = DoubleBoatProperty("anchorY")
    val anchorZProperty = DoubleBoatProperty("anchorZ")
    val deployedProperty = BooleanBoatProperty("deployed")

    val anchorDescentSpeed get() = 0.2

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        val active = activeProperty[from]
        if(active) {
            from.blockMovement(this)
        }
    }

    override fun update(from: IControllable) {
        val deployed = deployedProperty[from]
        if(!deployed)
            return
        val direction = anchorDirectionProperty[from]
        if(direction == 0)
            return
        val anchorX = anchorXProperty[from]
        val anchorY = anchorYProperty[from]
        val anchorZ = anchorZProperty[from]

        if(direction == -1) { // going down
            val nextY = anchorY + anchorDescentSpeed * direction
            anchorYProperty[from] = nextY
            val pos = BlockPos.Mutable(anchorX, nextY, anchorZ)
            val world = from.worldRef
            if(world.getBlockState(pos).canSurvive(world, pos)) {
                // stop descent
                anchorDirectionProperty[from] = 0
                activeProperty[from] = true
            }
        } else { // going up
            val dx = from.positionX - anchorX
            val dy = from.positionY - anchorY
            val dz = from.positionZ - anchorZ
            val totalLength = sqrt(dx*dx+dy*dy+dz*dz)
            val toAdvance = anchorDescentSpeed
            val length = minOf(toAdvance, totalLength)/totalLength
            val nextX = dx * length + anchorX
            val nextY = dy * length + anchorY
            val nextZ = dz * length + anchorZ
            anchorXProperty[from] = nextX
            anchorYProperty[from] = nextY
            anchorZProperty[from] = nextZ
            if(from.correspondingEntity.position().distanceToSqr(nextX, nextY, nextZ) < 1.0) { // going up & less than half a block away
                // stop
                anchorDirectionProperty[from] = 0
                deployedProperty[from] = false
            }
        }
    }

    override fun onAddition(to: IControllable) {
        activeProperty[to] = false
        deployedProperty[to] = false
        anchorXProperty[to] = 0.0
        anchorYProperty[to] = 0.0
        anchorZProperty[to] = 0.0
        anchorDirectionProperty[to] = 0
    }

    override fun createContainer(containerID: Int, player: PlayerEntity, boat: IControllable): ContainerBoatModule<*>? = EmptyModuleContainer(containerID, player.inventory, this, boat)

    override fun createGui(containerID: Int, player: PlayerEntity, boat: IControllable): Screen {
        return GuiAnchorModule(containerID, player.inventory, this, boat)
    }

    fun deploy(boat: IControllable, player: ServerPlayerEntity) {
        val deployed = deployedProperty[boat]
        if(deployed) {
            anchorDirectionProperty[boat] = 1
            activeProperty[boat] = false
        } else {
            deployedProperty[boat] = true
            anchorDirectionProperty[boat] = -1

            anchorXProperty[boat] = boat.positionX
            anchorYProperty[boat] = boat.positionY
            anchorZProperty[boat] = boat.positionZ
            player.displayClientMessage(spawnPointSet, true)
            player.setRespawnPosition(boat.worldRef.dimension(), BlockPos(boat.correspondingEntity.position()), boat.yaw, true, true)
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(Blocks.ANVIL.asItem(), 1)
    }
}