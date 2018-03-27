package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiBlockPlacer
import org.jglrxavpok.moarboats.common.containers.ContainerBlockPlacer
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids

object BlockPlacerModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "block_placer")
    override val usesInventory = true
    override val moduleSpot = Spot.Misc

    val blockPeriodProperty = IntBoatProperty("blockPeriod")
    val timerProperty = IntBoatProperty("timer")
    val BOAT_BEHIND get()= Vec3d(0.0, 0.0, 0.0625 * 25) // TODO: remove 'get()'

    // Row indices
    val TOP = 1
    val MIDDLE = 0
    val BOTTOM = -1

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        if(!from.inLiquid())
            return
        val timer = timerProperty[from]
        if(timer+1 >= blockPeriodProperty[from]) {
            timerProperty[from] -= blockPeriodProperty[from]
            placeBlock(BOTTOM, from)
            placeBlock(MIDDLE, from)
            placeBlock(TOP, from)
        } else {
            timerProperty[from] += 1
        }
    }

    private fun placeBlock(row: Int, boat: IControllable) {
        val pos = boat.localToWorld(BOAT_BEHIND)
        val blockPos = BlockPos.PooledMutableBlockPos.retain(pos.x, pos.y+row + .75f, pos.z)
        val inventoryRowStart = (-row)*5 +5
        firstValidStack(inventoryRowStart, boat)?.let { stack ->
            val itemBlock = stack.item as ItemBlock
            val world = boat.worldRef
            val block = itemBlock.block
            if(world.isAirBlock(blockPos) || Fluids.isUsualLiquidBlock(world.getBlockState(blockPos))) {
                if(world.mayPlace(block, blockPos, false, null, boat.correspondingEntity)) {
                    val succeeded = itemBlock.placeBlockAt(stack, null, world, blockPos, null, 0.5f, 0.5f, 0.5f, block.defaultState)
                    if(succeeded)
                        stack.shrink(1)
                }
            }
        }
        blockPos.release()
    }

    private fun firstValidStack(startIndex: Int, boat: IControllable): ItemStack? {
        val inv = boat.getInventory()
        return (0..4)
                .map { offset -> inv.getStackInSlot(startIndex+offset) }
                .filter { !it.isEmpty }
                .firstOrNull { it.item is ItemBlock }
    }

    override fun onAddition(to: IControllable) {
        blockPeriodProperty[to] = 1 // each block by default
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = ContainerBlockPlacer(player.inventory, this, boat)

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiBlockPlacer(player.inventory, this, boat)
    }
}