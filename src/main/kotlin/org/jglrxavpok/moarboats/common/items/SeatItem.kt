package org.jglrxavpok.moarboats.common.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

object SeatItem : MoarBoatsItem("seat") {

    val description = Component.translatable("item.seat.description")

    override fun appendHoverText(stack: ItemStack, worldIn: Level?, tooltip: MutableList<Component>, flagIn: TooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
        tooltip.add(description)
    }
}