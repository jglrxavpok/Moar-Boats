package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats

object RudderItem : Item() {
    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "rudder"
        registryName = ResourceLocation(MoarBoats.ModID, "rudder")
        maxStackSize = 64
    }
}