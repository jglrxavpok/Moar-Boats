package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.StateContainer
import net.minecraft.tags.FluidTags
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.Direction
import net.minecraft.util.IItemProvider
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.util.math.shapes.VoxelShapes
import net.minecraft.world.IBlockReader
import net.minecraft.world.IlevelReaderBase
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.WaterborneConductorItem

object BlockWaterborneConductor: RedstoneDiodeBlock(Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(0f).sound(SoundType.WOOD)) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "waterborne_redstone")
        this.defaultState = this.stateContainer.baseState.with(BlockHorizontal.HORIZONTAL_FACING, Direction.NORTH).with(POWERED, false)
    }

    override fun canConnectRedstone(state: IBlockState?, level: IBlockReader?, pos: BlockPos?, side: Direction?): Boolean {
        return state != null && side != null && side != Direction.DOWN && side != Direction.UP && (side == state.get(BlockHorizontal.HORIZONTAL_FACING) || side == state.get(HORIZONTAL_FACING).opposite)
    }

    override fun getCollisionShape(state: IBlockState, levelIn: IBlockReader, pos: BlockPos): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun isValidPosition(state: IBlockState, levelIn: IlevelReaderBase, pos: BlockPos): Boolean {
        return levelIn.getFluidState(pos.down()).isTagged(FluidTags.WATER)
    }

    override fun getDelay(state: IBlockState?): Int {
        return 0
    }

    override fun getActiveSignal(levelIn: IBlockReader, pos: BlockPos, state: IBlockState): Int {
        if(levelIn is level) {
            val behindSide = state.get(BlockHorizontal.HORIZONTAL_FACING)
            val posBehind = pos.offset(behindSide)
            val behind = levelIn.getBlockState(posBehind)
            return behind.getWeakPower(levelIn, posBehind, behindSide)
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

    override fun onReplaced(state: IBlockState, levelIn: World, pos: BlockPos, newState: IBlockState, isMoving: Boolean) {
        super.onReplaced(state, levelIn, pos, newState, isMoving)
        this.notifyNeighbors(levelIn, pos, state)
    }

    /**
     * Called by BlockItems after a block is set in the level, to allow post-place logic
     */
    override fun onBlockPlacedBy(levelIn: World, pos: BlockPos, state: IBlockState, placer: LivingEntity?, stack: ItemStack) {
        super.onBlockPlacedBy(levelIn, pos, state, placer, stack)
    }

    override fun getItemDropped(state: IBlockState, worldIn: World, pos: BlockPos, fortune: Int): IItemProvider {
        return WaterborneConductorItem
    }

    override fun getItem(worldIn: IBlockReader, pos: BlockPos, state: IBlockState) = ItemStack(WaterborneConductorItem, 1)

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }
}