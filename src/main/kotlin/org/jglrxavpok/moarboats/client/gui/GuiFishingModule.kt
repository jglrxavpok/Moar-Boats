package org.jglrxavpok.moarboats.client.gui

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule
import org.jglrxavpok.moarboats.common.modules.FishingModule

class GuiFishingModule(containerID: Int, playerInventory: Inventory, fishingModule: BoatModule, boat: IControllable):
        GuiModuleBase<ContainerFishingModule>(fishingModule, boat, playerInventory, ContainerFishingModule(containerID, playerInventory, fishingModule, boat)) {

    val missingStorage = Component.translatable("gui.fishing.missingStorage")
    val fishingModule = module as FishingModule

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/fishing.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        if(!fishingModule.readyProperty[boat]) {
            font.drawCenteredString(matrixStack, missingStorage, width/2, 20, 0xFF4040)
        }
    }
}