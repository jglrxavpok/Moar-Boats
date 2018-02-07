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

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/anchor.png")

    val deployButton = GuiButton(0,0,0,"")
    val deployedText = TextComponentTranslation("gui.anchor.deployed")
    val undeployedText = TextComponentTranslation("gui.anchor.undeploy")

    // TODO: Force player to sit in boat?

    override fun initGui() {
        super.initGui()
        deployButton.x = guiLeft
        deployButton.y = guiTop
        val deployText = if(boat.getState(module).getBoolean(AnchorModule.DEPLOYED)) deployedText else undeployedText
        deployButton.displayString = deployText.unformattedText
        addButton(deployButton)
    }

    override fun actionPerformed(button: GuiButton) {
        when(button) {
            deployButton -> {
                MoarBoats.network.sendToServer(C5DeployAnchor(boat.entityID, module.id))
            }
        }
    }
}