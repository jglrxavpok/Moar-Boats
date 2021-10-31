package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.BlockWaterborneComparator

object WaterborneComparatorItem : WaterborneItem("waterborne_comparator") {

    override val correspondingBlock = BlockWaterborneComparator
    private val descriptionText = TranslationTextComponent(MoarBoats.ModID+".tile.waterborne_comparator.description")

    override fun appendHoverText(stack: ItemStack?, player: World?, tooltip: MutableList<ITextComponent>, advanced: ITooltipFlag?) {
        tooltip.add(descriptionText)
    }
}