package org.jglrxavpok.moarboats.common

import net.minecraft.block.Block
import org.jglrxavpok.moarboats.common.blocks.*

object Blocks {

    val list = listOf<Block>(BlockUnpoweredWaterborneConductor, BlockPoweredWaterborneConductor,
            BlockBoatBattery, BlockEnergyLoader, BlockEnergyUnloader,
            BlockPoweredWaterborneComparator, BlockUnpoweredWaterborneComparator, BlockBoatTank,
            BlockFluidLoader, BlockFluidUnloader)
}