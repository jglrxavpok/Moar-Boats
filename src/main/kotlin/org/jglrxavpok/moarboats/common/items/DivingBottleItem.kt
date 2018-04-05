package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats

object DivingBottleItem : Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "diving_bottle"
        registryName = ResourceLocation(MoarBoats.ModID, "diving_bottle")
        maxStackSize = 64
    }

}