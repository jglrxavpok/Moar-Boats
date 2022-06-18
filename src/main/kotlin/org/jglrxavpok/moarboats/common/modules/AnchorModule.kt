package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
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

// TODO: remove
@Deprecated("To remove")
object AnchorModule: BoatModule(), BlockReason {

    override val id = ResourceLocation(MoarBoats.ModID, "anchor")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc
    override val isMenuInteresting = false
    val spawnPointSet = Component.translatable("gui.anchor.spawnPointSet")

    val activeProperty = BooleanBoatProperty("active")
    val anchorDirectionProperty = IntBoatProperty("anchorDirection")
    val anchorXProperty = DoubleBoatProperty("anchorX")
    val anchorYProperty = DoubleBoatProperty("anchorY")
    val anchorZProperty = DoubleBoatProperty("anchorZ")
    val deployedProperty = BooleanBoatProperty("deployed")

    val anchorDescentSpeed get() = 0.2

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
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
            val pos = BlockPos.MutableBlockPos(anchorX, nextY, anchorZ)
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

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? = EmptyModuleContainer(containerID, player.inventory, this, boat)

    override fun createGui(containerID: Int, player: Player, boat: IControllable): Screen {
        return GuiAnchorModule(containerID, player.inventory, this, boat)
    }

    fun deploy(boat: IControllable, player: ServerPlayer) {
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