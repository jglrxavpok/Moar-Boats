package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.inventory.MenuType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player

open class EmptyContainer(containerID: Int, playerInventory: Inventory, val isLarge: Boolean = false, val xStart: Int = 8, val containerType: MenuType<*> = ContainerTypes.Empty): ContainerBase<EmptyContainer>(containerType as MenuType<EmptyContainer>, containerID, playerInventory) {

    init {
        addPlayerSlots(isLarge, xStart)
    }

    override fun stillValid(playerIn: Player): Boolean {
        return true
    }
}