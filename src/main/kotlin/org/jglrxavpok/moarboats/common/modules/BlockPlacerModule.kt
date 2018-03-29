package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiBlockPlacer
import org.jglrxavpok.moarboats.common.containers.ContainerBlockPlacer
import org.jglrxavpok.moarboats.common.state.BlockPosProperty
import org.jglrxavpok.moarboats.common.state.DoubleBoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.use

object BlockPlacerModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "block_placer")
    override val usesInventory = true
    override val moduleSpot = Spot.Misc

    val blockPeriodProperty = DoubleBoatProperty("period")
    val lastBlockPositionProperty = BlockPosProperty("lastBlock")
    val BOAT_BEHIND = Vec3d(0.0, 0.0, 0.0625 * 25)

    // Row indices
    val TOP = 1
    val MIDDLE = 0
    val BOTTOM = -1

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        if(!from.inLiquid())
            return
        lastBlockPositionProperty[from].use { pos ->
            val period = blockPeriodProperty[from]
            if(pos.distanceSq(from.positionX, from.positionY, from.positionZ) >= period*period) {
                placeBlock(BOTTOM, from)
                placeBlock(MIDDLE, from)
                placeBlock(TOP, from)
                val newPos = BlockPos.PooledMutableBlockPos.retain(from.positionX, from.positionY, from.positionZ)
                lastBlockPositionProperty[from] = newPos
                newPos.release()
            }
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
            val newState = block.getStateFromMeta(stack.metadata)
            if(world.isAirBlock(blockPos) || Fluids.isUsualLiquidBlock(world.getBlockState(blockPos))) {
                if(world.mayPlace(block, blockPos, false, null, boat.correspondingEntity)) {
                    val succeeded = world.setBlockState(blockPos, newState, 11)
                    if (succeeded) {
                        try {
                            val state = world.getBlockState(blockPos)
                            if (state.block === block) {
                                setTileEntityNBT(world, blockPos, stack)
                                block.onBlockPlacedBy(world, blockPos, state, null, stack)
                            }
                        } catch (npe: NullPointerException) {
                            // some blocks do not like at all being placed by a machine apparently (eg chests)
                        }
                        stack.shrink(1)
                    }
                }
            }
        }
        blockPos.release()
    }

    // adapted from ItemBlock.java
    private fun setTileEntityNBT(worldIn: World, pos: BlockPos, stackIn: ItemStack) {
        val nbttagcompound = stackIn.getSubCompound("BlockEntityTag")

        if (nbttagcompound != null) {
            val tileentity = worldIn.getTileEntity(pos)

            if (tileentity != null) {
                val nbttagcompound1 = tileentity.writeToNBT(NBTTagCompound())
                val nbttagcompound2 = nbttagcompound1.copy()
                nbttagcompound1.merge(nbttagcompound)
                nbttagcompound1.setInteger("x", pos.x)
                nbttagcompound1.setInteger("y", pos.y)
                nbttagcompound1.setInteger("z", pos.z)

                if (nbttagcompound1 != nbttagcompound2) {
                    tileentity.readFromNBT(nbttagcompound1)
                    tileentity.markDirty()
                }
            }
        }
    }

    private fun firstValidStack(startIndex: Int, boat: IControllable): ItemStack? {
        val inv = boat.getInventory()
        return (0..4)
                .map { offset -> inv.getStackInSlot(startIndex+offset) }
                .filter { !it.isEmpty }
                .firstOrNull { it.item is ItemBlock }
    }

    override fun onAddition(to: IControllable) {
        blockPeriodProperty[to] = 0.5 // every block by default
    }

    fun changePeriod(boat: IControllable, period: Double) {
        blockPeriodProperty[boat] = period
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = ContainerBlockPlacer(player.inventory, this, boat)

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiBlockPlacer(player.inventory, this, boat)
    }
}
