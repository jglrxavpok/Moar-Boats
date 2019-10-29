package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

open class EmptyContainer(containerID: Int, playerInventory: PlayerInventory, val isLarge: Boolean = false, val xStart: Int = 8, val containerType: ContainerType<*> = ContainerTypes.Empty): ContainerBase<EmptyContainer>(containerType as ContainerType<EmptyContainer>, containerID, playerInventory) {

    init {
        addPlayerSlots(isLarge, xStart)
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}