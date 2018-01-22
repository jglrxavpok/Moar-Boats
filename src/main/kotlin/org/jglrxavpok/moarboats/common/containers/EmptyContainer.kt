package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container

object EmptyContainer: Container() {
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }
}