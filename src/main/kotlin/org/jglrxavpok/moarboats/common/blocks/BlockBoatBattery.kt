package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.MaterialColor
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.IBlockReader
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

object BlockBoatBattery: MoarBoatsBlock() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_battery")
    }

    override fun addInformation(stack: ItemStack, worldIn: IBlockReader?, tooltip: MutableList<ITextComponent>, advanced: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats)
    }

}