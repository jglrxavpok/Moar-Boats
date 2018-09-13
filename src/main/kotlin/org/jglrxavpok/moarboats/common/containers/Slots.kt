package org.jglrxavpok.moarboats.common.containers

import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import org.jglrxavpok.moarboats.common.items.ItemGoldenItinerary
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath

class SlotMap(inventory: IInventory, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {
    override fun isItemValid(stack: ItemStack): Boolean {
        return stack.item == Items.FILLED_MAP || stack.item == ItemGoldenItinerary || stack.item == ItemMapWithPath
    }
}

class SlotFishingRod(inventory: IInventory, index: Int, x: Int, y: Int): SlotOneItemType(inventory, index, x, y) {
    override val validItem = Items.FISHING_ROD
}