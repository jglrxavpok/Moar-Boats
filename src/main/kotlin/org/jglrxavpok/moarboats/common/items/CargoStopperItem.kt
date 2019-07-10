package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredCargoStopper
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterborneComparator
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterborneConductor

object CargoStopperItem : WaterborneItem("cargo_stopper") {

    override val correspondingBlock = BlockUnpoweredCargoStopper
    private val descriptionText = TextComponentTranslation(MoarBoats.ModID+".tile.cargo_stopper.description")

    override fun addInformation(stack: ItemStack?, player: World?, tooltip: MutableList<ITextComponent>, advanced: ITooltipFlag?) {
        tooltip.add(descriptionText)
    }
}