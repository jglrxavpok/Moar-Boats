package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World

object SeatItem : MoarBoatsItem("seat") {

    val description = TranslationTextComponent("item.seat.description")

    override fun appendHoverText(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
        tooltip.add(description)
    }
}