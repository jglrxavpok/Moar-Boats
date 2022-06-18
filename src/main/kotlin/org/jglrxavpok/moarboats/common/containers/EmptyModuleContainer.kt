package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

open class EmptyModuleContainer(containerID: Int, playerInventory: Inventory, module: BoatModule, boat: IControllable, val isLarge: Boolean = false, val xStart: Int = 8): ContainerBoatModule<EmptyModuleContainer>(module.containerType as MenuType<EmptyModuleContainer>, containerID, playerInventory, module, boat) {

    init {
        addPlayerSlots(isLarge, xStart)
    }

    override fun stillValid(playerIn: Player): Boolean {
        return true
    }
}