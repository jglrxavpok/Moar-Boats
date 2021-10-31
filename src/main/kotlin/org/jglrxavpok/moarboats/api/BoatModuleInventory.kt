package org.jglrxavpok.moarboats.api

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.IIntArray
import net.minecraft.util.IntArray
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.SSyncInventory

abstract class BoatModuleInventory(val inventoryName: String, val slotCount: Int, val boat: IControllable, val module: BoatModule, val list: NonNullList<ItemStack>):
        Inventory(slotCount) {

    protected abstract fun id2key(id: Int): String?
    abstract fun getFieldCount(): Int

    private inner class AdditionalDataArray: IIntArray {
        override fun getCount(): Int {
            return getFieldCount()
        }

        override fun get(index: Int): Int {
            return getField(index)
        }

        override fun set(index: Int, value: Int) {
            setField(index, value)
        }
    }

    var additionalData: IIntArray = AdditionalDataArray()

    fun getField(id: Int): Int {
        val key = id2key(id)
        if(key != null)
            return getModuleState().getInt(key)
        return -1
    }

    fun setField(id: Int, value: Int) {
        val key = id2key(id)
        if(key != null) {
            getModuleState().putInt(key, value)
            saveModuleState()
        }
    }

    fun getModuleState() = boat.getState(module)
    fun saveModuleState() {
        boat.saveState(module)
    }

    // from Inventory
    fun add(stack: ItemStack): ItemStack {
        val itemstack = stack.copy()

        for(i in 0 until this.containerSize) {
            val stackInSlot = this.getItem(i)

            if(stackInSlot.isEmpty) {
                this.setItem(i, itemstack)
                this.setChanged()
                return ItemStack.EMPTY
            }

            if(ItemStack.isSame(stackInSlot, itemstack)) {
                val maxStackSize = this.maxStackSize.coerceAtMost(stackInSlot.maxStackSize)
                val canFit = itemstack.count.coerceAtMost(maxStackSize - stackInSlot.count)

                if(canFit > 0) {
                    stackInSlot.grow(canFit)
                    itemstack.shrink(canFit)

                    if(itemstack.isEmpty) {
                        this.setChanged()
                        return ItemStack.EMPTY
                    }
                }
            }
        }

        if(itemstack.count != stack.count) {
            this.setChanged()
        }

        return itemstack
    }

    fun syncToClient() {
        if(!boat.worldRef.isClientSide) {
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
        for(i in 0 until inv.containerSize) {
            val itemstack = inv.getItem(i)
            if(itemstack.isEmpty)
                continue
            for(j in 0 until this.containerSize) {
                val stackInSlot = this.getItem(j)

                if(stackInSlot.isEmpty) {
                    return true
                }

                if(ItemStack.isSame(stackInSlot, itemstack)) {
                    val maxStackSize = this.maxStackSize.coerceAtMost(stackInSlot.maxStackSize)
                    val canFit = maxStackSize - stackInSlot.count > 0

                    if(canFit) {
                        return true
                    }
                }
            }
        }
        return false
    }

}
