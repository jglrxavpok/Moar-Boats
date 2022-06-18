package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*

class UtilityWorkbenchContainer(windowId: Int, playerInv: Inventory, pos: ContainerLevelAccess): CraftingMenu(windowId, playerInv, pos) {
    override fun stillValid(playerIn: Player): Boolean {
        return true
    }
}

class UtilityGrindstoneContainer(windowId: Int, playerInv: Inventory, pos: ContainerLevelAccess): GrindstoneMenu(windowId, playerInv, pos) {
    override fun stillValid(playerIn: Player): Boolean {
        return true
    }
}

class UtilityLoomContainer(windowId: Int, playerInv: Inventory, pos: ContainerLevelAccess): LoomMenu(windowId, playerInv, pos) {
    override fun stillValid(playerIn: Player): Boolean {
        return true
    }
}

class UtilityCartographyTableContainer(windowId: Int, playerInv: Inventory, pos: ContainerLevelAccess): CartographyTableMenu(windowId, playerInv, pos) {
    override fun stillValid(playerIn: Player): Boolean {
        return true
    }
}

class UtilityStonecutterContainer(windowId: Int, playerInv: Inventory, pos: ContainerLevelAccess): StonecutterMenu(windowId, playerInv, pos) {
    override fun stillValid(playerIn: Player): Boolean {
        return true
    }
}