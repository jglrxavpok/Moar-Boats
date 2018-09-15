package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class ContainerMappingTable(val te: TileEntityMappingTable, val playerInv: InventoryPlayer): ContainerBase(playerInv) {

    init {
        addPlayerSlots(false)

        addSlotToContainer(SlotMappingTable(te.inventory, 0, 0, 0))
    }

    private inner class SlotMappingTable(inventory: IInventory, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {
        override fun isItemValid(stack: ItemStack): Boolean {
            return stack.item == ItemMapWithPath || stack.item == ItemGoldenTicket
        }
    }
}