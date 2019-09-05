package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

class GuiChestModule(playerInventory: PlayerInventory, engine: BoatModule, boat: IControllable):
        GuiModuleBase<ContainerChestModule>(engine, boat, playerInventory, ContainerChestModule(playerInventory, engine, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/chest.png")
}