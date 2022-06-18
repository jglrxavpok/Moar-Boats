package org.jglrxavpok.moarboats.common.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MBBlocks

class CargoStopperItem : WaterborneItem("cargo_stopper") {

    override val correspondingBlock = MBBlocks.CargoStopper.get()
    private val descriptionText = Component.translatable(MoarBoats.ModID+".tile.cargo_stopper.description")

    override fun appendHoverText(stack: ItemStack?, player: Level?, tooltip: MutableList<Component>, advanced: TooltipFlag?) {
        tooltip.add(descriptionText)
    }
}