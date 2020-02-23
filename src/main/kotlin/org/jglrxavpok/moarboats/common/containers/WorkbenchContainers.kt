package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.*
import net.minecraft.util.IWorldPosCallable

class UtilityWorkbenchContainer(windowId: Int, playerInv: PlayerInventory, pos: IWorldPosCallable): WorkbenchContainer(windowId, playerInv, pos) {
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}

class UtilityGrindstoneContainer(windowId: Int, playerInv: PlayerInventory, pos: IWorldPosCallable): GrindstoneContainer(windowId, playerInv, pos) {
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}

class UtilityLoomContainer(windowId: Int, playerInv: PlayerInventory, pos: IWorldPosCallable): LoomContainer(windowId, playerInv, pos) {
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}

class UtilityCartographyTableContainer(windowId: Int, playerInv: PlayerInventory, pos: IWorldPosCallable): CartographyContainer(windowId, playerInv, pos) {
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}

class UtilityStonecutterContainer(windowId: Int, playerInv: PlayerInventory, pos: IWorldPosCallable): StonecutterContainer(windowId, playerInv, pos) {
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}