package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.widget.button.Button
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.common.network.CDeployAnchor

class GuiAnchorModule(playerInventory: PlayerInventory, anchor: BoatModule, boat: IControllable):
        GuiModuleBase<EmptyContainer>(anchor, boat, playerInventory, EmptyContainer(playerInventory)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    val deployButton = Button(0,0, 140, 20, "") {
        MoarBoats.network.sendToServer(CDeployAnchor(boat.id, module.id))
    }
    val deployedText = TranslationTextComponent("gui.anchor.deployed")
    val undeployedText = TranslationTextComponent("gui.anchor.deploy")
    val movingAnchorText = TranslationTextComponent("gui.anchor.moving")
    val descText = TranslationTextComponent("gui.anchor.desc")
    val anchor = module as AnchorModule

    override fun init() {
        super.init()
        deployButton.x = guiLeft+imageWidth/2-70
        deployButton.y = guiTop+20
        addButton(deployButton)
    }

    override fun tick() {
        super.tick()
        if(anchor.anchorDirectionProperty[boat] != 0) {
            deployButton.message = movingAnchorText.coloredString
            deployButton.active = false
        } else {
            val deployText = if(anchor.deployedProperty[boat]) deployedText else undeployedText
            deployButton.message = deployText.coloredString
            deployButton.active = true
        }
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        font.drawWordWrap(descText.coloredString, 0+20, 0+50, imageWidth-40, 0xF0F0F0)
    }

}