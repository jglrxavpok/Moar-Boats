package org.jglrxavpok.moarboats.common.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

object HelmItem: Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "helm"
        registryName = ResourceLocation(MoarBoats.ModID, "helm")
        maxStackSize = 64
    }
}