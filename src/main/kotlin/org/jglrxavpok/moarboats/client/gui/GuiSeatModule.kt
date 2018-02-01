package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

class GuiSeatModule(playerInventory: InventoryPlayer, seat: BoatModule, boat: IControllable):
        GuiModuleBase(seat, boat, playerInventory, EmptyContainer(playerInventory)) {

    val enjoyTheTrip = TextComponentTranslation("gui.seat.enjoy")
    val nothingToDo = TextComponentTranslation("gui.seat.nothingToDo")
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/seat.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        drawCenteredString(fontRenderer, nothingToDo.unformattedText, 0, 0, 0xFFFFFF)
        drawCenteredString(fontRenderer, enjoyTheTrip.unformattedText, 0, 10, 0xFFFFFF)
    }
}