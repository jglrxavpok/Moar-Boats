package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.*
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.BlockFluidBase
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.WaterborneConductorItem
import java.util.*

val BlockPoweredWaterboneConductor = BlockWaterborneConductor(powered = true)
val BlockUnpoweredWaterboneConductor = BlockWaterborneConductor(powered = false)

class BlockWaterborneConductor(powered: Boolean): BlockRedstoneDiode(powered) {
    init {
        val id = "waterborne_redstone_${if(powered) "" else "un"}powered"
        registryName = ResourceLocation(MoarBoats.ModID, id)
        unlocalizedName = id
        this.defaultState = this.blockState.baseState.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH)
    }

    override fun canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
        return side != null && side != EnumFacing.DOWN && side != EnumFacing.UP
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
        return BlockUnpoweredWaterboneConductor.defaultState.withProperty(FACING, enumfacing)
    }

    override fun getPoweredState(unpoweredState: IBlockState): IBlockState {
        val enumfacing = unpoweredState.getValue(FACING) as EnumFacing
        return BlockPoweredWaterboneConductor.defaultState.withProperty(FACING, enumfacing)
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
                true -> worldIn.setBlockState(pos, BlockUnpoweredWaterboneConductor.defaultState.withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)))
                false -> worldIn.setBlockState(pos, BlockPoweredWaterboneConductor.defaultState.withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)))
            }
            notifyNeighbors(worldIn, pos, state)
        }
    }

    override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int) = WaterborneConductorItem

    override fun getItem(worldIn: World?, pos: BlockPos?, state: IBlockState?) = ItemStack(WaterborneConductorItem, 1)
}