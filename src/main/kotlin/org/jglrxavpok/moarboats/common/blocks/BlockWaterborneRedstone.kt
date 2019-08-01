package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.BlockRedstoneDiode
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.state.StateContainer
import net.minecraft.tags.FluidTags
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.IItemProvider
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.util.math.shapes.VoxelShapes
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorldReaderBase
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.WaterborneConductorItem

object BlockWaterborneConductor: BlockRedstoneDiode(Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(0f).sound(SoundType.WOOD)) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "waterborne_redstone")
        this.defaultState = this.stateContainer.baseState.with(BlockHorizontal.HORIZONTAL_FACING, EnumFacing.NORTH).with(POWERED, false)
    }

    override fun canConnectRedstone(state: IBlockState?, world: IBlockReader?, pos: BlockPos?, side: EnumFacing?): Boolean {
        return state != null && side != null && side != EnumFacing.DOWN && side != EnumFacing.UP && (side == state.get(BlockHorizontal.HORIZONTAL_FACING) || side == state.get(HORIZONTAL_FACING).opposite)
    }

    override fun getCollisionShape(state: IBlockState, worldIn: IBlockReader, pos: BlockPos): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun isValidPosition(state: IBlockState, worldIn: IWorldReaderBase, pos: BlockPos): Boolean {
        return worldIn.getFluidState(pos.down()).isTagged(FluidTags.WATER)
    }

    override fun getDelay(state: IBlockState?): Int {
        return 0
    }

    override fun getActiveSignal(worldIn: IBlockReader, pos: BlockPos, state: IBlockState): Int {
        if(worldIn is World) {
            val behindSide = state.get(BlockHorizontal.HORIZONTAL_FACING)
            val posBehind = pos.offset(behindSide)
            val behind = worldIn.getBlockState(posBehind)
            return behind.getWeakPower(worldIn, posBehind, behindSide)
        }
        return 0
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block, IBlockState>) {
        builder.add(BlockHorizontal.HORIZONTAL_FACING, POWERED)
    }

    override fun onReplaced(state: IBlockState, worldIn: World, pos: BlockPos, newState: IBlockState, isMoving: Boolean) {
        super.onReplaced(state, worldIn, pos, newState, isMoving)
        this.notifyNeighbors(worldIn, pos, state)
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase?, stack: ItemStack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
    }

    override fun getItemDropped(state: IBlockState, worldIn: World, pos: BlockPos, fortune: Int): IItemProvider {
        return WaterborneConductorItem
    }

    override fun getItem(worldIn: IBlockReader, pos: BlockPos, state: IBlockState) = ItemStack(WaterborneConductorItem, 1)

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }
}