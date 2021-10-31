package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

object ChunkLoaderItem : MoarBoatsItem("chunk_loader") {

    override fun appendHoverText(stack: ItemStack?, player: World?, tooltip: MutableList<ITextComponent>, advanced: ITooltipFlag?) {
        super.appendHoverText(stack, player, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats)
    }

}