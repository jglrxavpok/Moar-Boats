package org.jglrxavpok.moarboats.common.containers

import net.minecraft.item.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

abstract class SlotOneItemType(inventory: IInventory, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {

    abstract val validItem: Item

    override fun isItemValid(stack: ItemStack): Boolean {
        return stack.item == validItem
    }
}