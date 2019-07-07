package org.jglrxavpok.moarboats.api

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryBasic
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.SSyncInventory

abstract class BoatModuleInventory(val inventoryName: String, val slotCount: Int, val boat: IControllable, val module: BoatModule, val list: NonNullList<ItemStack>):
        InventoryBasic(TextComponentString(inventoryName) /* TODO: change to TextComponent */, slotCount) {

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
            MoarBoats.network.send(PacketDistributor.ALL.noArg(), SSyncInventory(boat.entityID, module.id, list))
        }
    }

    /**
     * Checks that any item from the given inventory can be added or merged with one of the stacks inside this inventory.
     * Used for special case of full hoppers above locked boats.
     *
     * WARNING: This only checks if a SINGLE item can fit, not a whole stack
     */
    fun canAddAnyFrom(inv: IInventory): Boolean {
        for(i in 0 until inv.sizeInventory) {
            val itemstack = inv.getStackInSlot(i)
            if(itemstack.isEmpty)
                continue
            for (j in 0 until this.sizeInventory) {
                val stackInSlot = this.getStackInSlot(j)

                if (stackInSlot.isEmpty) {
                    return true;
                }

                if (ItemStack.areItemsEqual(stackInSlot, itemstack)) {
                    val maxStackSize = Math.min(this.inventoryStackLimit, stackInSlot.maxStackSize)
                    val canFit = maxStackSize - stackInSlot.count > 0;

                    if (canFit) {
                        return true
                    }
                }
            }
        }
        return false
    }

}