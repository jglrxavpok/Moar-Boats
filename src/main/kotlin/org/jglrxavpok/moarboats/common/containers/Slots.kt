package org.jglrxavpok.moarboats.common.containers

import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath

class SlotMap(inventory: IInventory, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {
    override fun isItemValid(stack: ItemStack): Boolean {
        return stack.item == Items.FILLED_MAP || stack.item == ItemGoldenTicket || stack.item == ItemMapWithPath
    }

    override fun getItemStackLimit(stack: ItemStack?): Int {
        return 1
    }
}

class SlotFishingRod(inventory: IInventory, index: Int, x: Int, y: Int): SlotOneItemType(inventory, index, x, y) {
    override val validItem = Items.FISHING_ROD
}