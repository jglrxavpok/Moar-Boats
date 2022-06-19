package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.FishingModule

class GuiFishingModule(menuType: MenuType<ContainerFishingModule>, containerID: Int, playerInventory: Inventory, fishingModule: BoatModule, boat: IControllable):
        GuiModuleBase<ContainerFishingModule>(fishingModule, boat, playerInventory, ContainerFishingModule(menuType, containerID, playerInventory, fishingModule, boat)) {

    val missingStorage = Component.translatable("gui.fishing.missingStorage")
    val fishingModule = module as FishingModule

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/fishing.png")

    override fun drawModuleForeground(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(poseStack, mouseX, mouseY)
        if(!fishingModule.readyProperty[boat]) {
            font.drawCenteredString(poseStack, missingStorage, width/2, 20, 0xFF4040)
        }
    }
}