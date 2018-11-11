package org.jglrxavpok.moarboats.integration.opencomputers.client

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiModuleBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.integration.opencomputers.BoatMachineHost
import org.jglrxavpok.moarboats.integration.opencomputers.ComputerModule
import org.jglrxavpok.moarboats.integration.opencomputers.OpenComputerPlugin

class GuiComputerModule(val player: EntityPlayer, boat: IControllable): GuiModuleBase(ComputerModule, boat, player.inventory, EmptyContainer(player.inventory, true)) {
    val host: BoatMachineHost = OpenComputerPlugin.getHost(boat)!!

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/opencomputer/background.png")

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        var y = 0
        for(elem in host.internalComponents()) {
            itemRender.renderItemIntoGUI(elem, 0, y)
            y+=20
        }
    }
}