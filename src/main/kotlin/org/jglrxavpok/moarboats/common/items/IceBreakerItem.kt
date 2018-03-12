package org.jglrxavpok.moarboats.common.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

object IceBreakerItem: Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "icebreaker"
        registryName = ResourceLocation(MoarBoats.ModID, "icebreaker")
        maxStackSize = 64
    }
}