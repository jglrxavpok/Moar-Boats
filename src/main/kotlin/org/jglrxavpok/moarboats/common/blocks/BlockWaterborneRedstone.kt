package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DiodeBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.jglrxavpok.moarboats.common.MBItems

class BlockWaterborneConductor: DiodeBlock(Properties.of(Material.DECORATION).noOcclusion().strength(0f).sound(SoundType.WOOD)) {
    init {
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(POWERED, false))
    }

    override fun canConnectRedstone(state: BlockState?, level: BlockGetter?, pos: BlockPos?, side: Direction?): Boolean {
        return state != null && side != null && side != Direction.DOWN && side != Direction.UP && (side == state.getValue(FACING) || side == state.getValue(FACING).opposite)
    }

    override fun getCollisionShape(state: BlockState, worldIn: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.empty()
    }

    override fun canSurvive(state: BlockState, levelIn: LevelReader, pos: BlockPos): Boolean {
        return levelIn.getFluidState(pos.below()).`is`(FluidTags.WATER)
    }

    override fun getDelay(state: BlockState?): Int {
        return 0
    }

    override fun getDirectSignal(blockState: BlockState, blockAccess: BlockGetter?, pos: BlockPos?, side: Direction): Int {
        return if (!blockState.getValue(POWERED)) {
            0
        } else {
            if (canConnectRedstone(blockState, blockAccess, pos, side)) getOutputSignal(blockAccess!!, pos!!, blockState) else 0
        }
    }

    override fun getOutputSignal(levelIn: BlockGetter, pos: BlockPos, state: BlockState): Int {
        val behindSide = state.getValue(FACING)
        val posBehind = pos.relative(behindSide)
        val behind = levelIn.getBlockState(posBehind)
        return behind.getSignal(levelIn, posBehind, behindSide)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, POWERED)
    }

    override fun onRemove(state: BlockState, levelIn: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, levelIn, pos, newState, isMoving)
        this.checkTickOnNeighbor(levelIn, pos, state)
    }

    /**
     * Called by BlockItems after a block is set in the level, to allow post-place logic
     */
    override fun setPlacedBy(levelIn: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(levelIn, pos, state, placer, stack)
    }

    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        return mutableListOf(ItemStack(MBItems.WaterborneConductorItem.get(), 1))
    }

    override fun getCloneItemStack(worldIn: BlockGetter, pos: BlockPos, state: BlockState) = ItemStack(MBItems.WaterborneConductorItem.get(), 1)

}