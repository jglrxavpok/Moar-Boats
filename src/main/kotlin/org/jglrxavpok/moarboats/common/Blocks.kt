package org.jglrxavpok.moarboats.common

import net.minecraft.block.Block
import org.jglrxavpok.moarboats.common.blocks.*

object Blocks {

    val list = listOf<Block>(BlockWaterborneConductor,
            BlockBoatBattery, BlockEnergyLoader, BlockEnergyUnloader,
            BlockWaterborneComparator,
            BlockCargoStopper,
            BlockBoatTank,
            // FIXME Will be back when fluids are back BlockFluidLoader, BlockFluidUnloader,
            BlockMappingTable)
}