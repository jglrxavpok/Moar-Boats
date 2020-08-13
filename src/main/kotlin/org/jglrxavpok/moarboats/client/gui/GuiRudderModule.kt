package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.widget.button.Button
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.RudderModule
import org.jglrxavpok.moarboats.common.network.CChangeRudderBlocking

class GuiRudderModule(containerID: Int, playerInventory: PlayerInventory, anchor: BoatModule, boat: IControllable):
        GuiModuleBase<EmptyModuleContainer>(anchor, boat, playerInventory, EmptyModuleContainer(containerID, playerInventory, anchor, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    val blockButton = Button(0, 0, 140, 20, StringTextComponent("")) {
        MoarBoats.network.sendToServer(CChangeRudderBlocking(boat.entityID, module.id))
    }
    val blockingText = TranslationTextComponent("moarboats.gui.rudder.blocking")
    val notBlockingText = TranslationTextComponent("moarboats.gui.rudder.nonblocking")
    val rudder = module as RudderModule

    override fun init() {
        super.init()
        blockButton.x = guiLeft + xSize / 2 - 70
        blockButton.y = guiTop + 30
        addButton(blockButton)
    }

    override fun tick() {
        super.tick()
        val deployText = if(rudder.BlockingProperty[boat]) blockingText else notBlockingText
        blockButton.active = true
        blockButton.message = deployText.formatted()
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
    }
}
