package org.jglrxavpok.moarboats.common

import net.minecraft.block.Block
import org.jglrxavpok.moarboats.common.blocks.BlockBoatBattery
import org.jglrxavpok.moarboats.common.blocks.BlockPoweredWaterboneConductor
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterboneConductor

object Blocks {

    val list = listOf<Block>(BlockUnpoweredWaterboneConductor, BlockPoweredWaterboneConductor, BlockBoatBattery)
}