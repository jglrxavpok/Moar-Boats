package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.Blocks
import net.minecraft.client.gui.screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiAnchorModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import org.jglrxavpok.moarboats.common.state.DoubleBoatProperty
import org.jglrxavpok.moarboats.common.state.IntBoatProperty

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
            val pos = BlockPos.PooledMutableBlockPos.acquire(anchorX, nextY, anchorZ)
            val world = from.worldRef
            if(world.getBlockState(pos).isTopSolid(world, pos)) {
                // stop descent
                anchorDirectionProperty[from] = 0
                activeProperty[from] = true
            }
            pos.close()
        } else { // going up
            val dx = from.positionX - anchorX
            val dy = from.positionY - anchorY
            val dz = from.positionZ - anchorZ
            val totalLength = Math.sqrt(dx*dx+dy*dy+dz*dz)
            val toAdvance = anchorDescentSpeed
            val length = minOf(toAdvance, totalLength)/totalLength
            val nextX = dx * length + anchorX
            val nextY = dy * length + anchorY
            val nextZ = dz * length + anchorZ
            anchorXProperty[from] = nextX
            anchorYProperty[from] = nextY
            anchorZProperty[from] = nextZ
            if(from.correspondingEntity.positionVector.squareDistanceTo(nextX, nextY, nextZ) < 1.0) { // going up & less than half a block away
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

    override fun createContainer(player: PlayerEntity, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: PlayerEntity, boat: IControllable): Screen {
        return GuiAnchorModule(player.inventory, this, boat)
    }

    fun deploy(boat: IControllable, player: PlayerEntity) {
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
            player.sendMessage(spawnPointSet, true)
            player.setSpawnPoint(boat.correspondingEntity.position, true)
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(Blocks.ANVIL.asItem(), 1)
    }
}