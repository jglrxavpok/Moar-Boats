package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.FluidTags
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DiodeBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

abstract class WaterborneRedstoneBlock: DiodeBlock(Properties.of(Material.DECORATION).noOcclusion().randomTicks().strength(0f).sound(SoundType.WOOD)) {

    protected val ShapeNW = Shapes.join(
        Block.box(1.0, -3.0, 1.0, 15.0, 0.0, 15.0),
        Block.box(4.0, 0.0, 2.0, 12.0, 1.0, 14.0),
        BooleanOp.OR)

    protected val ShapeWE = Shapes.join(
        Block.box(1.0, -3.0, 1.0, 15.0, 0.0, 15.0),
        Block.box(2.0, 0.0, 4.0, 14.0, 1.0, 12.0),
        BooleanOp.OR)

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(POWERED, false))
    }

    override fun getShape(state: BlockState?, blockGetter: BlockGetter?, pos: BlockPos?, collisionContext: CollisionContext?): VoxelShape? {
        if(state?.getValue(FACING) == Direction.WEST || state?.getValue(FACING) == Direction.EAST) {
            return ShapeWE
        }
        return ShapeNW
    }

    override fun isRandomlyTicking(state: BlockState) = true

    override fun getCollisionShape(state: BlockState, worldIn: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.empty()
    }

    override fun canSurvive(state: BlockState, worldIn: LevelReader, pos: BlockPos): Boolean {
        return worldIn.getFluidState(pos.below()).`is`(FluidTags.WATER)
    }

    override fun getDelay(state: BlockState?): Int {
        return 0
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, POWERED)
    }

}