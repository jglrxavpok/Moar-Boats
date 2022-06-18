package org.jglrxavpok.moarboats.common.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

class ChunkLoaderItem : MoarBoatsItem("chunk_loader") {

    override fun appendHoverText(stack: ItemStack?, player: Level?, tooltip: MutableList<Component>, advanced: TooltipFlag?) {
        super.appendHoverText(stack, player, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats)
    }

}