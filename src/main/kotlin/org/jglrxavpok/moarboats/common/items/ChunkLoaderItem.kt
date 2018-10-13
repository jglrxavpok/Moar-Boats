package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

object ChunkLoaderItem : Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "boat_chunk_loader"
        registryName = ResourceLocation(MoarBoats.ModID, "chunk_loader")
        maxStackSize = 64
    }

    override fun addInformation(stack: ItemStack?, player: World?, tooltip: MutableList<String>, advanced: ITooltipFlag?) {
        super.addInformation(stack, player, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats.unformattedText)
    }

}