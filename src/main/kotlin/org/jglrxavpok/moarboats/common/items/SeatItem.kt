package org.jglrxavpok.moarboats.common.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

object SeatItem : Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "seat"
        registryName = ResourceLocation(MoarBoats.ModID, "seat")
        maxStackSize = 64
    }
}