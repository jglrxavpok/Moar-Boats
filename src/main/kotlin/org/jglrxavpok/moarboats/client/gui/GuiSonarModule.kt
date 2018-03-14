package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

class GuiSonarModule(playerInventory: InventoryPlayer, sonar: BoatModule, boat: IControllable):
        GuiModuleBase(sonar, boat, playerInventory, EmptyContainer(playerInventory)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/sonar.png")
}