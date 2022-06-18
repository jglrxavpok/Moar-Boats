package org.jglrxavpok.moarboats.client.gui

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer

class GuiNoConfigModule(containerID: Int, playerInventory: Inventory, module: BoatModule, boat: IControllable):
        GuiModuleBase<EmptyModuleContainer>(module, boat, playerInventory, EmptyModuleContainer(containerID, playerInventory, boat)) {

    val enjoyTheTrip = Component.translatable("gui.seat.enjoy")
    val nothingToDo = Component.translatable("gui.seat.nothingToDo")
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        font.drawCenteredString(matrixStack, nothingToDo/*.formatted()*/, xSize/2, 30, 0xFFFFFF)
        font.drawCenteredString(matrixStack, enjoyTheTrip/*.formatted()*/, xSize/2, 40, 0xFFFFFF)
    }
}