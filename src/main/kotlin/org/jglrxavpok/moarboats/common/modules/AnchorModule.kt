package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiAnchorModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import org.jglrxavpok.moarboats.common.state.DoubleBoatProperty
import org.jglrxavpok.moarboats.common.state.IntBoatProperty

object AnchorModule: BoatModule() {

    override val id = ResourceLocation(MoarBoats.ModID, "anchor")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc

    val spawnPointSet = TextComponentTranslation("gui.anchor.spawnPointSet")

    val activeProperty = BooleanBoatProperty("active")
    val anchorDirectionProperty = IntBoatProperty("anchorDirection")
    val anchorXProperty = DoubleBoatProperty("anchorX")
    val anchorYProperty = DoubleBoatProperty("anchorY")
    val anchorZProperty = DoubleBoatProperty("anchorZ")
    val deployedProperty = BooleanBoatProperty("deployed")

    val anchorDescentSpeed get() = 0.2

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        val active = activeProperty[from]
        if(active) {
            from.blockMovement()
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
            val pos = BlockPos.PooledMutableBlockPos.retain(anchorX, nextY, anchorZ)
            val world = from.worldRef
            if(world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) {
                // stop descent
                anchorDirectionProperty[from] = 0
                activeProperty[from] = true
            }
            pos.release()
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

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiAnchorModule(player.inventory, this, boat)
    }

    fun deploy(boat: IControllable, player: EntityPlayer) {
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
            player.sendStatusMessage(spawnPointSet, true)
            player.setSpawnPoint(boat.correspondingEntity.position, true)
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(ItemBlock.getItemFromBlock(Blocks.ANVIL), 1)
    }
}