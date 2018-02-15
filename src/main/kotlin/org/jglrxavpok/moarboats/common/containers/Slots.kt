package org.jglrxavpok.moarboats.common.containers

import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class SlotMap(inventory: IInventory, index: Int, x: Int, y: Int): SlotOneItemType(inventory, index, x, y) {
    override val validItem = Items.FILLED_MAP
}

class SlotFishingRod(inventory: IInventory, index: Int, x: Int, y: Int): SlotOneItemType(inventory, index, x, y) {
    override val validItem = Items.FISHING_ROD
}