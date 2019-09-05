package org.jglrxavpok.moarboats.extensions

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object Fluids {

    fun getLiquidHeight(level: World, pos: BlockPos): Float {
        val fluidState = level.getFluidState(pos)
        return when {
            fluidState.isEmpty -> 0f
            else -> pos.y+fluidState.height
        }
    }

    fun getLiquidLocalLevel(level: World, pos: BlockPos) = level.getFluidState(pos).level

    fun getBlockLiquidHeight(level: World, pos: BlockPos): Float {
        val state = level.getFluidState(pos)
        val stateAbove = level.getFluidState(pos.above())
        return when {
            !stateAbove.isEmpty -> 1.0f
            else -> 1.0f - state.height
        }
    }

    fun isUsualLiquidBlock(level: World, pos: BlockPos) = !level.getFluidState(pos).isEmpty
}