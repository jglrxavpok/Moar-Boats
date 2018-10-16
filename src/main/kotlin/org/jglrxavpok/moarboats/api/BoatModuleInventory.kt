package org.jglrxavpok.moarboats.api

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryBasic
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.S7SyncInventory

abstract class BoatModuleInventory(val inventoryName: String, val slotCount: Int, val boat: IControllable, val module: BoatModule, val list: NonNullList<ItemStack>):
        InventoryBasic(inventoryName, true, slotCount) {

    fun getModuleState() = boat.getState(module)
    fun saveModuleState() {
        boat.saveState(module)
    }

    // from InventoryBasic
    fun add(stack: ItemStack): ItemStack {
        val itemstack = stack.copy()

        for (i in 0 until this.sizeInventory) {
            val stackInSlot = this.getStackInSlot(i)

            if (stackInSlot.isEmpty) {
                this.setInventorySlotContents(i, itemstack)
                this.markDirty()
                return ItemStack.EMPTY
            }

            if (ItemStack.areItemsEqual(stackInSlot, itemstack)) {
                val maxStackSize = Math.min(this.inventoryStackLimit, stackInSlot.maxStackSize)
                val canFit = Math.min(itemstack.count, maxStackSize - stackInSlot.count)

                if (canFit > 0) {
                    stackInSlot.grow(canFit)
                    itemstack.shrink(canFit)

                    if (itemstack.isEmpty) {
                        this.markDirty()
                        return ItemStack.EMPTY
                    }
                }
            }
        }

        if (itemstack.count != stack.count) {
            this.markDirty()
        }

        return itemstack
    }

    fun syncToClient() {
        if(!boat.worldRef.isRemote) {
            MoarBoats.network.sendToAll(S7SyncInventory(boat.entityID, module.id, list))
        }
    }

}