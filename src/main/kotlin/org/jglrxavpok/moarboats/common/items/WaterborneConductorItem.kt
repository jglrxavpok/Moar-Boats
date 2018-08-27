package org.jglrxavpok.moarboats.common.items

import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterborneComparator
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterborneConductor

object WaterborneConductorItem : WaterborneItem() {

    override val correspondingBlock = BlockUnpoweredWaterborneConductor
    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "waterborne_conductor"
        registryName = ResourceLocation(MoarBoats.ModID, "waterborne_conductor")
        maxStackSize = 64
    }
}