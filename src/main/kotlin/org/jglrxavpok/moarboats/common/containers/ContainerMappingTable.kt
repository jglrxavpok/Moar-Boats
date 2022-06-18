package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class ContainerMappingTable(containerID: Int, val te: TileEntityMappingTable, val playerInv: Inventory): ContainerBase<ContainerMappingTable>(ContainerTypes.MappingTable, containerID, playerInv) {

    init {
        addSlot(SlotMappingTable(te.inventory, 0, 8, 8))
        addPlayerSlots(true)
    }

    override fun setItem(slotID: Int, stateID: Int, stack: ItemStack?) {
        super.setItem(slotID, stateID, stack)
        broadcastChanges()
    }

    override fun slotsChanged(inventoryIn: Container?) {
        super.slotsChanged(inventoryIn)
        broadcastChanges()
    }

    override fun clicked(slotId: Int, dragType: Int, clickTypeIn: ClickType?, player: Player?) {
        super.clicked(slotId, dragType, clickTypeIn, player)
        broadcastChanges()
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.slots[index]

        if (slot != null && slot.hasItem()) {
            val itemstack1 = slot.item
            itemstack = itemstack1.copy()

            if (index != 0) {
                if (itemstack1.item == MapItemWithPath || itemstack1.item == ItemGoldenTicket) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 1..28) {
                    if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 28..36 && !this.moveItemStackTo(itemstack1, 1, 27, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemstack1, 1, 37, false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.isEmpty) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (itemstack1.count == itemstack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(playerIn, itemstack1)
        }

        return itemstack
    }

    private inner class SlotMappingTable(inventory: Container, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {
        override fun mayPlace(stack: ItemStack): Boolean {
            return stack.item == MapItemWithPath || stack.item == ItemGoldenTicket
        }

        override fun getMaxStackSize(): Int {
            return 1
        }
    }
}