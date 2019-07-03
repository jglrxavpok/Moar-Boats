package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterborneComparator
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterborneConductor

object WaterborneComparatorItem : WaterborneItem() {

    override val correspondingBlock = BlockUnpoweredWaterborneComparator
    private val descriptionText = TextComponentTranslation(MoarBoats.ModID+".tile.waterborne_comparator.description")

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "waterborne_comparator"
        registryName = ResourceLocation(MoarBoats.ModID, "waterborne_comparator")
        maxStackSize = 64
    }

    override fun addInformation(stack: ItemStack?, player: World?, tooltip: MutableList<String>, advanced: ITooltipFlag?) {
        tooltip.add(descriptionText.formattedText)
    }
}