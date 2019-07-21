package org.jglrxavpok.moarboats.extensions

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object Fluids {

    fun getLiquidHeight(world: World, pos: BlockPos): Float {
        val fluidState = world.getFluidState(pos)
        return when {
            fluidState.isEmpty -> 0f
            else -> pos.y+fluidState.height
        }
    }

    fun getLiquidLocalLevel(world: World, pos: BlockPos) = world.getFluidState(pos).level

    fun getBlockLiquidHeight(world: World, pos: BlockPos): Float {
        val state = world.getFluidState(pos)
        val stateAbove = world.getFluidState(pos.up())
        return when {
            !stateAbove.isEmpty -> 1.0f
            else -> 1.0f - state.height
        }
    }

    fun isUsualLiquidBlock(world: World, pos: BlockPos) = !world.getFluidState(pos).isEmpty
}