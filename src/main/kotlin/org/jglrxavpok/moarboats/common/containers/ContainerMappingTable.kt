package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class ContainerMappingTable(val te: TileEntityMappingTable, val playerInv: InventoryPlayer): ContainerBase(playerInv) {

    init {
        addSlot(SlotMappingTable(te.inventory, 0, 8, 8))
        addPlayerSlots(true)
    }

    override fun putStackInSlot(slotID: Int, stack: ItemStack?) {
        super.putStackInSlot(slotID, stack)
        detectAndSendChanges()
    }

    override fun onCraftMatrixChanged(inventoryIn: IInventory?) {
        super.onCraftMatrixChanged(inventoryIn)
        detectAndSendChanges()
    }

    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType?, player: EntityPlayer?): ItemStack {
        val r = super.slotClick(slotId, dragType, clickTypeIn, player)
        detectAndSendChanges()
        return r
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index != 0) {
                if (itemstack1.item == ItemMapWithPath || itemstack1.item == ItemGoldenTicket) {
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
            return stack.item == ItemMapWithPath || stack.item == ItemGoldenTicket
        }

        override fun getSlotStackLimit(): Int {
            return 1
        }
    }
}