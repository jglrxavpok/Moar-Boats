package org.jglrxavpok.moarboats.common.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MBBlocks

class WaterborneComparatorItem : WaterborneItem("waterborne_comparator") {

    override val correspondingBlock = MBBlocks.WaterborneComparator.get()
    private val descriptionText = Component.translatable(MoarBoats.ModID+".tile.waterborne_comparator.description")

    override fun appendHoverText(stack: ItemStack?, player: Level?, tooltip: MutableList<Component>, advanced: TooltipFlag?) {
        tooltip.add(descriptionText)
    }
}