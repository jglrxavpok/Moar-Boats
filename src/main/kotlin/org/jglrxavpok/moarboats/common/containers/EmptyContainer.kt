package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container

class EmptyContainer(playerInventory: InventoryPlayer): ContainerBase(playerInventory) {

    init {
        addPlayerSlots(isLarge = false)
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }
}