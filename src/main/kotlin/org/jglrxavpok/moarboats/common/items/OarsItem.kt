package org.jglrxavpok.moarboats.common.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

object OarsItem: Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "oars"
        registryName = ResourceLocation(MoarBoats.ModID, "oars")
        maxStackSize = 64
    }
}