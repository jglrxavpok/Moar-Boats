package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

class GuiNoConfigModule(playerInventory: PlayerInventory, module: BoatModule, boat: IControllable):
        GuiModuleBase<EmptyContainer>(module, boat, playerInventory, EmptyContainer(playerInventory)) {

    val enjoyTheTrip = TranslationTextComponent("gui.seat.enjoy")
    val nothingToDo = TranslationTextComponent("gui.seat.nothingToDo")
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        drawCenteredString(font, nothingToDo.formattedText, imageWidth/2, 30, 0xFFFFFF)
        drawCenteredString(font, enjoyTheTrip.formattedText, imageWidth/2, 40, 0xFFFFFF)
    }
}