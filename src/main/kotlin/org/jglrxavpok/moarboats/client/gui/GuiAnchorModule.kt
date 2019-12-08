package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.widget.button.Button
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.common.network.CDeployAnchor

class GuiAnchorModule(containerID: Int, playerInventory: PlayerInventory, anchor: BoatModule, boat: IControllable):
        GuiModuleBase<EmptyModuleContainer>(anchor, boat, playerInventory, EmptyModuleContainer(containerID, playerInventory, anchor, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    val deployButton = Button(0,0, 140, 20, "") {
        MoarBoats.network.sendToServer(CDeployAnchor(boat.entityID, module.id))
    }
    val deployedText = TranslationTextComponent("gui.anchor.deployed")
    val undeployedText = TranslationTextComponent("gui.anchor.deploy")
    val movingAnchorText = TranslationTextComponent("gui.anchor.moving")
    val descText = TranslationTextComponent("gui.anchor.desc")
    val anchor = module as AnchorModule

    override fun init() {
        super.init()
        deployButton.x = guiLeft+width/2-70
        deployButton.y = guiTop+20
        addButton(deployButton)
    }

    override fun tick() {
        super.tick()
        if(anchor.anchorDirectionProperty[boat] != 0) {
            deployButton.message = movingAnchorText.formattedText
            deployButton.active = false
        } else {
            val deployText = if(anchor.deployedProperty[boat]) deployedText else undeployedText
            deployButton.message = deployText.formattedText
            deployButton.active = true
        }
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        font.drawSplitString(descText.formattedText, 0+20, 0+50, width-40, 0xF0F0F0)
    }

}