package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.BlockLiquid
import net.minecraft.block.BlockRedstoneDiode
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.items.WaterborneComparatorItem
import java.util.*

object BlockPoweredWaterborneComparator: BlockWaterborneComparator(true)
object BlockUnpoweredWaterborneComparator: BlockWaterborneComparator(false)

open class BlockWaterborneComparator(val powered: Boolean): BlockRedstoneDiode(powered) {
    init {
        val id = "waterborne_comparator_${if(powered) "" else "un"}lit"
        registryName = ResourceLocation(MoarBoats.ModID, id)
        unlocalizedName = id
        this.defaultState = this.blockState.baseState.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH)
        tickRandomly = true
    }

    override fun canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
        return side != null && side != EnumFacing.DOWN && side != EnumFacing.UP && (side == state.getValue(FACING) || side == state.getValue(FACING).opposite)
    }

    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
        return Block.NULL_AABB
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        val blockBelow = worldIn.getBlockState(pos.down()).block
        return blockBelow is BlockLiquid || blockBelow is BlockFluidBase
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos): Boolean {
        return canPlaceBlockAt(worldIn, pos)
    }

    override fun getDelay(state: IBlockState?): Int {
        return 0
    }

    override fun getUnpoweredState(poweredState: IBlockState): IBlockState {
        val enumfacing = poweredState.getValue(FACING) as EnumFacing
        return BlockUnpoweredWaterborneComparator.defaultState.withProperty(FACING, enumfacing)
    }

    override fun getPoweredState(unpoweredState: IBlockState): IBlockState {
        val enumfacing = unpoweredState.getValue(FACING) as EnumFacing
        return BlockPoweredWaterborneComparator.defaultState.withProperty(FACING, enumfacing)
    }

    override fun getWeakPower(state: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        if(blockAccess is World && side == state.getValue(FACING)) {
            val world = blockAccess
            val aabb = AxisAlignedBB(pos.offset(state.getValue(BlockHorizontal.FACING)))
            val entities = world.getEntitiesWithinAABB(BasicBoatEntity::class.java, aabb) { e -> e != null && e.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) }
            val first = entities.firstOrNull()
            return first?.let { calcRedstoneFromInventory(it.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) } ?: 0
        }
        return 0
    }

    override fun requiresUpdates(): Boolean {
        return true
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random?) {
        val produceSignal = shouldBePowered(worldIn, pos, state)
        when {
            produceSignal && !isRepeaterPowered -> worldIn.setBlockState(pos, BlockPoweredWaterborneComparator.defaultState.withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)))
            !produceSignal && isRepeaterPowered -> worldIn.setBlockState(pos, BlockUnpoweredWaterborneComparator.defaultState.withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)))
        }
        worldIn.scheduleUpdate(pos, this, 2)
        notifyNeighbors(worldIn, pos, state)
    }

    private fun calcRedstoneFromInventory(inv: IItemHandler?): Int {
        if (inv == null) {
            return 0
        } else {
            var i = 0
            var f = 0.0f

            for (slotIndex in 0 until inv.slots) {
                val itemstack = inv.getStackInSlot(slotIndex)

                if (!itemstack.isEmpty) {
                    f += itemstack.count.toFloat() / Math.min(inv.getSlotLimit(slotIndex), itemstack.maxStackSize).toFloat()
                    ++i
                }
            }

            f /= inv.slots.toFloat()
            return MathHelper.floor(f * 14.0f) + if (i > 0) 1 else 0
        }
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BlockHorizontal.FACING)
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(BlockHorizontal.FACING, EnumFacing.getHorizontal(meta))
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BlockHorizontal.FACING) as EnumFacing).horizontalIndex
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        this.notifyNeighbors(worldIn, pos, state)
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        this.notifyNeighbors(worldIn, pos, state)
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)
        if(shouldBePowered(worldIn, pos, state) != isRepeaterPowered) {
            when(!isRepeaterPowered) {
                true -> worldIn.setBlockState(pos, BlockUnpoweredWaterborneComparator.defaultState.withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)), 2)
                false -> worldIn.setBlockState(pos, BlockPoweredWaterborneComparator.defaultState.withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)), 2)
            }
            notifyNeighbors(worldIn, pos, state)
        }
        worldIn.scheduleUpdate(pos, this, 2)
    }

    override fun shouldBePowered(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        return getWeakPower(state, worldIn, pos, state.getValue(BlockHorizontal.FACING)) > 0
    }

    override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int) = WaterborneComparatorItem

    override fun getItem(worldIn: World?, pos: BlockPos?, state: IBlockState?) = ItemStack(WaterborneComparatorItem, 1)

    override fun getWeakChanges(world: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }

}