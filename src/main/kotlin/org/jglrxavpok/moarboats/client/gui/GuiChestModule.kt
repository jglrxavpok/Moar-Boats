package org.jglrxavpok.moarboats.client.gui

import net.minecraft.world.entity.player.Inventory
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

class GuiChestModule(containerID: Int, playerInventory: Inventory, engine: BoatModule, boat: IControllable):
        GuiModuleBase<ContainerChestModule>(engine, boat, playerInventory, ContainerChestModule(containerID, playerInventory, engine, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/chest.png")
}