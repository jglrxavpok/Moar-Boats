package org.jglrxavpok.moarboats.common.items

import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterborneComparator
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterborneConductor

object WaterborneConductorItem : WaterborneItem("waterborne_conductor") {

    override val correspondingBlock = BlockUnpoweredWaterborneConductor
}