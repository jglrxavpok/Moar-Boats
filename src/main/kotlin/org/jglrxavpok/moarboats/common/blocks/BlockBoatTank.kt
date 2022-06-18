package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

class BlockBoatTank: MoarBoatsBlock({ noOcclusion() }) {
    override fun getLightEmission(state: BlockState, level: BlockGetter, pos: BlockPos): Int {
        return 0
    }

    override fun appendHoverText(stack: ItemStack, level: BlockGetter?, tooltip: MutableList<Component>, advanced: TooltipFlag) {
        super.appendHoverText(stack, level, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats)
    }

}