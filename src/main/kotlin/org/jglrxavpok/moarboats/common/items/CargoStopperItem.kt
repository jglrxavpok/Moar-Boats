package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.BlockCargoStopper

object CargoStopperItem : WaterborneItem("cargo_stopper") {

    override val correspondingBlock = BlockCargoStopper
    private val descriptionText = TranslationTextComponent(MoarBoats.ModID+".tile.cargo_stopper.description")

    override fun appendHoverText(stack: ItemStack?, player: World?, tooltip: MutableList<ITextComponent>, advanced: ITooltipFlag?) {
        tooltip.add(descriptionText)
    }
}