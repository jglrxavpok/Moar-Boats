package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats

object SeatItem : Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "seat"
        registryName = ResourceLocation(MoarBoats.ModID, "seat")
        maxStackSize = 64
    }

    val description = TextComponentTranslation("item.seat.description")

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add(description.unformattedText)
    }
}