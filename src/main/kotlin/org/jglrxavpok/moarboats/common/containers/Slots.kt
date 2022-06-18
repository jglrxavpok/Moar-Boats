package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.items.MapItemWithPath

class SlotMap(inventory: Container, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {

    override fun mayPlace(stack: ItemStack): Boolean {
        return stack.item == Items.FILLED_MAP || stack.item is ItemPath
    }

    override fun getMaxStackSize(stack: ItemStack): Int {
        return 1
    }
}

class SlotFishingRod(inventory: Container, index: Int, x: Int, y: Int): SlotOneItemType(inventory, index, x, y) {
    override val validItem = Items.FISHING_ROD
}