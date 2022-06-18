package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

abstract class SlotOneItemType(inventory: Container, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {

    abstract val validItem: Item

    override fun mayPlace(stack: ItemStack): Boolean {
        return stack.item == validItem
    }
}