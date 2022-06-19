package org.jglrxavpok.moarboats.client.gui

import net.minecraft.world.entity.player.Inventory
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.MenuType
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer

class GuiChestModule(menuType: MenuType<ContainerChestModule>, containerID: Int, playerInventory: Inventory, engine: BoatModule, boat: IControllable):
        GuiModuleBase<ContainerChestModule>(engine, boat, playerInventory, ContainerChestModule(menuType, containerID, playerInventory, engine, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/chest.png")
}