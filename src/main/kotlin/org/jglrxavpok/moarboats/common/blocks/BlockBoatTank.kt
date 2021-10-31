package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.IBlockReader
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

object BlockBoatTank: MoarBoatsBlock({ noOcclusion() }) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_tank")
    }

    override fun getLightValue(state: BlockState, level: IBlockReader, pos: BlockPos): Int {
        return 0
    }

    override fun appendHoverText(stack: ItemStack, level: IBlockReader?, tooltip: MutableList<ITextComponent>, advanced: ITooltipFlag) {
        super.appendHoverText(stack, level, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats)
    }

}