package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

object BlockBoatBattery: Block(MoarBoats.MachineMaterial) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_battery")
        unlocalizedName = "boat_battery"
        setCreativeTab(MoarBoats.CreativeTab)
        setHardness(0.5f)
    }

    override fun addInformation(stack: ItemStack?, player: World?, tooltip: MutableList<String>, advanced: ITooltipFlag?) {
        super.addInformation(stack, player, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats.unformattedText)
    }

}