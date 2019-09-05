package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container

open class EmptyContainer(playerInventory: PlayerInventory, val isLarge: Boolean = false, val xStart: Int = 8): ContainerBase(playerInventory) {

    init {
        addPlayerSlots(isLarge, xStart)
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}