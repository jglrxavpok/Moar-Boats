package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.RudderModule
import org.jglrxavpok.moarboats.common.network.CChangeRudderBlocking

class GuiRudderModule(menuType: MenuType<EmptyModuleContainer>, containerID: Int, playerInventory: Inventory, anchor: BoatModule, boat: IControllable):
        GuiModuleBase<EmptyModuleContainer>(anchor, boat, playerInventory, EmptyModuleContainer(menuType, containerID, playerInventory, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/nothing.png")

    val blockButton = Button(0, 0, 140, 20, Component.literal("")) {
        MoarBoats.network.sendToServer(CChangeRudderBlocking(boat.entityID, module.id))
    }
    val blockingText = Component.translatable("moarboats.gui.rudder.blocking")
    val notBlockingText = Component.translatable("moarboats.gui.rudder.nonblocking")
    val rudder = module as RudderModule

    override fun init() {
        super.init()
        blockButton.x = guiLeft + xSize / 2 - 70
        blockButton.y = guiTop + 30
        addRenderableWidget(blockButton)
    }

    override fun containerTick() {
        super.containerTick()
        val deployText = if(rudder.BlockingProperty[boat]) blockingText else notBlockingText
        blockButton.active = true
        blockButton.message = deployText/*.formatted()*/
    }
}
