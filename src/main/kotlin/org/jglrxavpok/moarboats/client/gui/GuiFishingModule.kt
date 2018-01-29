package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

class GuiFishingModule(playerInventory: InventoryPlayer, fishingModule: BoatModule, boat: IControllable):
        GuiModuleBase(fishingModule, boat, playerInventory, ContainerFishingModule(playerInventory, fishingModule, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/fishing.png")
}