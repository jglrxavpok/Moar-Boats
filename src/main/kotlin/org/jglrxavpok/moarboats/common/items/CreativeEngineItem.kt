package org.jglrxavpok.moarboats.common.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

object CreativeEngineItem: Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "creative_engine"
        registryName = ResourceLocation(MoarBoats.ModID, "creative_engine")
        maxStackSize = 64
    }
}