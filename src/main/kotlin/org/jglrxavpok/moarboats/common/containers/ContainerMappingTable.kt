package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class ContainerMappingTable(val te: TileEntityMappingTable, val playerInv: PlayerInventory): ContainerBase(playerInv) {

    init {
        addSlot(SlotMappingTable(te.inventory, 0, 8, 8))
        addPlayerSlots(true)
    }

    override fun putStackInSlot(slotID: Int, stack: ItemStack?) {
        super.putStackInSlot(slotID, stack)
        broadcastChanges()
    }

    override fun onCraftMatrixChanged(inventoryIn: IInventory?) {
        super.onCraftMatrixChanged(inventoryIn)
        broadcastChanges()
    }

    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType?, player: PlayerEntity?): ItemStack {
        val r = super.slotClick(slotId, dragType, clickTypeIn, player)
        broadcastChanges()
        return r
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.item
            itemstack = itemstack1.copy()

            if (index != 0) {
                if (itemstack1.item == MapItemWithPath || itemstack1.item == ItemGoldenTicket) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 1..28) {
                    if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 28..36 && !this.mergeItemStack(itemstack1, 1, 27, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(itemstack1, 1, 37, false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.isEmpty) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (itemstack1.count == itemstack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(playerIn, itemstack1)
        }

        return itemstack
    }

    private inner class SlotMappingTable(inventory: IInventory, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {
        override fun isItemValid(stack: ItemStack): Boolean {
            return stack.item == MapItemWithPath || stack.item == ItemGoldenTicket
        }

        override fun getSlotStackLimit(): Int {
            return 1
        }
    }
}