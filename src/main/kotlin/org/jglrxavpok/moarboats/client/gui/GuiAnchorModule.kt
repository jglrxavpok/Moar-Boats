package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.resources.ResourceLocation
import net.minecraft.network.chat.Component.translatable
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.common.network.CDeployAnchor

class GuiAnchorModule(containerID: Int, playerInventory: Inventory, anchor: BoatModule, boat: IControllable):
        GuiModuleBase<EmptyModuleContainer>(anchor, boat, playerInventory, EmptyModuleContainer(containerID, playerInventory, anchor, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    val deployButton = Button(0,0, 140, 20, Component.literal("")) {
        MoarBoats.network.sendToServer(CDeployAnchor(boat.entityID, module.id))
    }
    val deployedText = Component.translatable("gui.anchor.deployed")
    val undeployedText = Component.translatable("gui.anchor.deploy")
    val movingAnchorText = Component.translatable("gui.anchor.moving")
    val descText = Component.translatable("gui.anchor.desc")
    val anchor = module as AnchorModule

    override fun init() {
        super.init()
        deployButton.x = guiLeft+width/2-70
        deployButton.y = guiTop+20
        addWidget(deployButton)
    }

    override fun containerTick() {
        super.containerTick()
        if(anchor.anchorDirectionProperty[boat] != 0) {
            deployButton.message = movingAnchorText
            deployButton.active = false
        } else {
            val deployText = if(anchor.deployedProperty[boat]) deployedText else undeployedText
            deployButton.message = deployText
            deployButton.active = true
        }
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        font.drawWordWrap(descText, 0+20, 0+50, width-40, 0xF0F0F0)
    }

}