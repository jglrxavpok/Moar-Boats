package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.IBlockReader
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

object BlockBoatTank: MoarBoatsBlock() {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_tank")
    }

    override fun getOpacity(state: IBlockState, level: IBlockReader, pos: BlockPos): Int {
        return 0
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun addInformation(stack: ItemStack, level: IBlockReader?, tooltip: MutableList<ITextComponent>, advanced: ITooltipFlag) {
        super.addInformation(stack, level, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats)
    }

}