package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

open class EmptyModuleContainer(containerID: Int, playerInventory: PlayerInventory, module: BoatModule, boat: IControllable, val isLarge: Boolean = false, val xStart: Int = 8): ContainerBoatModule<EmptyModuleContainer>(module.containerType as ContainerType<EmptyModuleContainer>, containerID, playerInventory, module, boat) {

    init {
        addPlayerSlots(isLarge, xStart)
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}