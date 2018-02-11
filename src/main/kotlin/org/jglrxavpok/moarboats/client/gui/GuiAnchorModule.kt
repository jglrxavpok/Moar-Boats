package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.common.network.C5DeployAnchor

class GuiAnchorModule(playerInventory: InventoryPlayer, anchor: BoatModule, boat: IControllable):
        GuiModuleBase(anchor, boat, playerInventory, EmptyContainer(playerInventory)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    val deployButton = GuiButton(0,0,0, 140, 20, "")
    val deployedText = TextComponentTranslation("gui.anchor.deployed")
    val undeployedText = TextComponentTranslation("gui.anchor.deploy")
    val movingAnchorText = TextComponentTranslation("gui.anchor.moving")

    override fun initGui() {
        super.initGui()
        deployButton.x = guiLeft+xSize/2-70
        deployButton.y = guiTop+40
        addButton(deployButton)
    }

    override fun updateScreen() {
        super.updateScreen()
        val state = boat.getState(module)
        if(state.getInteger(AnchorModule.ANCHOR_DIRECTION) != 0) {
            deployButton.displayString = movingAnchorText.unformattedText
            deployButton.enabled = false
        } else {
            val deployText = if(state.getBoolean(AnchorModule.DEPLOYED)) deployedText else undeployedText
            deployButton.enabled = true
            deployButton.displayString = deployText.unformattedText
        }
    }

    override fun actionPerformed(button: GuiButton) {
        when(button) {
            deployButton -> {
                MoarBoats.network.sendToServer(C5DeployAnchor(boat.entityID, module.id))
            }
        }
    }
}