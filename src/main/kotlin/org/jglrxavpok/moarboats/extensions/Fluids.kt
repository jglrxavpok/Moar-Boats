package org.jglrxavpok.moarboats.extensions

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

object Fluids {

    fun getLiquidHeight(level: Level, pos: BlockPos): Float {
        val fluidState = level.getFluidState(pos)
        return when {
            fluidState.isEmpty -> 0f
            else -> pos.y+fluidState.ownHeight
        }
    }

    fun getLiquidLocalLevel(level: Level, pos: BlockPos) = level.getFluidState(pos).amount

    fun getBlockLiquidHeight(level: Level, pos: BlockPos): Float {
        val state = level.getFluidState(pos)
        val stateAbove = level.getFluidState(pos.above())
        return when {
            !stateAbove.isEmpty -> 1.0f
            else -> 1.0f - state.ownHeight
        }
    }

    fun isUsualLiquidBlock(level: Level, pos: BlockPos) = !level.getFluidState(pos).isEmpty
}