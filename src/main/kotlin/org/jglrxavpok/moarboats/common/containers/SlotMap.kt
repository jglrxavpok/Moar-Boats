package org.jglrxavpok.moarboats.common.containers

import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

class SlotMap(inventory: IInventory, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return stack.item == Items.FILLED_MAP
    }
}