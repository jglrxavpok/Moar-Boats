package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootContext
import net.minecraft.state.StateContainer
import net.minecraft.tags.FluidTags
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.shapes.ISelectionContext
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.util.math.shapes.VoxelShapes
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.WaterborneConductorItem

object BlockWaterborneConductor: RedstoneDiodeBlock(Properties.of(Material.DECORATION).noOcclusion().strength(0f).sound(SoundType.WOOD)) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "waterborne_redstone")
        this.registerDefaultState(this.defaultBlockState().setValue(HorizontalBlock.FACING, Direction.NORTH).setValue(POWERED, false))
    }

    override fun canConnectRedstone(state: BlockState?, level: IBlockReader?, pos: BlockPos?, side: Direction?): Boolean {
        return state != null && side != null && side != Direction.DOWN && side != Direction.UP && (side == state.getValue(HorizontalBlock.FACING) || side == state.getValue(HorizontalBlock.FACING).opposite)
    }

    override fun getCollisionShape(state: BlockState, worldIn: IBlockReader, pos: BlockPos, context: ISelectionContext): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun isValidPosition(state: BlockState, levelIn: IWorldReader, pos: BlockPos): Boolean {
        return levelIn.getFluidState(pos.below()).isTagged(FluidTags.WATER)
    }

    override fun getDelay(state: BlockState?): Int {
        return 0
    }

    override fun getWeakPower(blockState: BlockState, blockAccess: IBlockReader?, pos: BlockPos?, side: Direction): Int {
        return if (!blockState.getValue(POWERED)) {
            0
        } else {
            if (canConnectRedstone(blockState, blockAccess, pos, side)) getActiveSignal(blockAccess!!, pos!!, blockState) else 0
        }
    }

    override fun getActiveSignal(levelIn: IBlockReader, pos: BlockPos, state: BlockState): Int {
        val behindSide = state.getValue(HorizontalBlock.FACING)
        val posBehind = pos.offset(behindSide)
        val behind = levelIn.getBlockState(posBehind)
        return behind.getWeakPower(levelIn, posBehind, behindSide)
    }

    override fun createBlockStateDefinition(builder: StateContainer.Builder<Block, BlockState>) {
        builder.add(HorizontalBlock.FACING, POWERED)
    }

    override fun onReplaced(state: BlockState, levelIn: World, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onReplaced(state, levelIn, pos, newState, isMoving)
        this.notifyNeighbors(levelIn, pos, state)
    }

    /**
     * Called by BlockItems after a block is set in the level, to allow post-place logic
     */
    override fun onBlockPlacedBy(levelIn: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.onBlockPlacedBy(levelIn, pos, state, placer, stack)
    }

    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        return mutableListOf(ItemStack(WaterborneConductorItem, 1))
    }

    override fun getItem(worldIn: IBlockReader, pos: BlockPos, state: BlockState) = ItemStack(WaterborneConductorItem, 1)

}