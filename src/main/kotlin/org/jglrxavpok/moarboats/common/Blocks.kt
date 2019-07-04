package org.jglrxavpok.moarboats.common

import net.minecraft.block.Block
import org.jglrxavpok.moarboats.common.blocks.*

object Blocks {

    val list = listOf<Block>(BlockUnpoweredWaterborneConductor, BlockPoweredWaterborneConductor,
            BlockBoatBattery, BlockEnergyLoader, BlockEnergyUnloader,
            BlockPoweredWaterborneComparator, BlockUnpoweredWaterborneComparator,
            BlockPoweredCargoStopper, BlockUnpoweredCargoStopper,
            BlockBoatTank,
            // FIXME Will be back when fluids are back BlockFluidLoader, BlockFluidUnloader,
            BlockMappingTable)
}