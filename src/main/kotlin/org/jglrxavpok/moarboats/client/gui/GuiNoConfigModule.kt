package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer

class GuiNoConfigModule(containerID: Int, playerInventory: PlayerInventory, module: BoatModule, boat: IControllable):
        GuiModuleBase<EmptyModuleContainer>(module, boat, playerInventory, EmptyModuleContainer(containerID, playerInventory, module, boat)) {

    val enjoyTheTrip = TranslationTextComponent("gui.seat.enjoy")
    val nothingToDo = TranslationTextComponent("gui.seat.nothingToDo")
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        textRenderer.drawCenteredString(matrixStack, nothingToDo.formatted(), xSize/2, 30, 0xFFFFFF)
        textRenderer.drawCenteredString(matrixStack, enjoyTheTrip.formatted(), xSize/2, 40, 0xFFFFFF)
    }
}