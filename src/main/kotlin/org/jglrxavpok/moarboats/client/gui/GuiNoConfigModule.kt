package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

class GuiNoConfigModule(playerInventory: InventoryPlayer, module: BoatModule, boat: IControllable):
        GuiModuleBase(module, boat, playerInventory, EmptyContainer(playerInventory)) {

    val enjoyTheTrip = TextComponentTranslation("gui.seat.enjoy")
    val nothingToDo = TextComponentTranslation("gui.seat.nothingToDo")
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        drawCenteredString(fontRenderer, nothingToDo.formattedText, xSize/2, 30, 0xFFFFFF)
        drawCenteredString(fontRenderer, enjoyTheTrip.formattedText, xSize/2, 40, 0xFFFFFF)
    }
}