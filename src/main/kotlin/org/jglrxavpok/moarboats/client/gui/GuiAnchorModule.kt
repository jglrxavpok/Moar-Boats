package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.common.network.CDeployAnchor

class GuiAnchorModule(playerInventory: InventoryPlayer, anchor: BoatModule, boat: IControllable):
        GuiModuleBase(anchor, boat, playerInventory, EmptyContainer(playerInventory)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    val deployButton = object: GuiButton(0,0,0, 140, 20, "") {
        override fun onClick(mouseX: Double, mouseY: Double) {
            MoarBoats.network.sendToServer(CDeployAnchor(boat.entityID, module.id))
        }
    }
    val deployedText = TextComponentTranslation("gui.anchor.deployed")
    val undeployedText = TextComponentTranslation("gui.anchor.deploy")
    val movingAnchorText = TextComponentTranslation("gui.anchor.moving")
    val descText = TextComponentTranslation("gui.anchor.desc")
    val anchor = module as AnchorModule

    override fun initGui() {
        super.initGui()
        deployButton.x = guiLeft+xSize/2-70
        deployButton.y = guiTop+20
        addButton(deployButton)
    }

    override fun tick() {
        super.tick()
        if(anchor.anchorDirectionProperty[boat] != 0) {
            deployButton.displayString = movingAnchorText.formattedText
            deployButton.enabled = false
        } else {
            val deployText = if(anchor.deployedProperty[boat]) deployedText else undeployedText
            deployButton.enabled = true
            deployButton.displayString = deployText.formattedText
        }
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        fontRenderer.drawSplitString(descText.formattedText, 0+20, 0+50, xSize-40, 0xF0F0F0)
    }

}