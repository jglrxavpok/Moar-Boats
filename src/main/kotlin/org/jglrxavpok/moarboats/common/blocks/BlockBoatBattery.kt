package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.BlockGetter
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

class BlockBoatBattery: MoarBoatsBlock() {

    override fun appendHoverText(stack: ItemStack, worldIn: BlockGetter?, tooltip: MutableList<Component>, advanced: TooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats)
    }

}