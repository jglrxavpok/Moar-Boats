package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.modules.FishingModule

class GuiFishingModule(playerInventory: InventoryPlayer, fishingModule: BoatModule, boat: IControllable):
        GuiModuleBase(fishingModule, boat, playerInventory, ContainerFishingModule(playerInventory, fishingModule, boat)) {

    val missingStorage = TextComponentTranslation("gui.fishing.missingStorage")
    val fishingModule = module as FishingModule

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/fishing.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        if(!fishingModule.readyProperty[boat]) {
            drawCenteredString(fontRenderer, missingStorage.formattedText, xSize/2, 20, 0xFF4040)
        }
    }
}