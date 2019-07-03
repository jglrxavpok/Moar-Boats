package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.modules.RudderModule
import org.jglrxavpok.moarboats.common.network.CChangeRudderBlocking
import org.jglrxavpok.moarboats.common.network.CDeployAnchor

class GuiRudderModule(playerInventory: InventoryPlayer, anchor: BoatModule, boat: IControllable):
        GuiModuleBase(anchor, boat, playerInventory, EmptyContainer(playerInventory)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    val blockButton = GuiButton(0,0,0, 140, 20, "")
    val blockingText = TextComponentTranslation("moarboats.gui.rudder.blocking")
    val notBlockingText = TextComponentTranslation("moarboats.gui.rudder.nonblocking")
    val rudder = module as RudderModule

    override fun initGui() {
        super.initGui()
        blockButton.x = guiLeft+xSize/2-70
        blockButton.y = guiTop+30
        addButton(blockButton)
    }

    override fun tick() {
        super.tick()
        val deployText = if(rudder.BlockingProperty[boat]) blockingText else notBlockingText
        blockButton.enabled = true
        blockButton.displayString = deployText.formattedText
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
    }

    override fun actionPerformed(button: GuiButton) {
        when(button) {
            blockButton -> {
                MoarBoats.network.sendToServer(CChangeRudderBlocking(boat.entityID, module.id))
            }
        }
    }
}