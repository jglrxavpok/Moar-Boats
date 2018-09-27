package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class ContainerMappingTable(val te: TileEntityMappingTable, val playerInv: InventoryPlayer): ContainerBase(playerInv) {

    init {
        addSlotToContainer(SlotMappingTable(te.inventory, 0, 8, 8))
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

    private inner class SlotMappingTable(inventory: IInventory, index: Int, x: Int, y: Int): Slot(inventory, index, x, y) {
        override fun isItemValid(stack: ItemStack): Boolean {
            return stack.item == ItemMapWithPath || stack.item == ItemGoldenTicket
        }
    }
}