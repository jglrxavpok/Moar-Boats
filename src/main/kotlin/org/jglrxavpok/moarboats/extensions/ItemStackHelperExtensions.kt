package org.jglrxavpok.moarboats.extensions

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.NonNullList

fun saveInventory(compound: CompoundNBT, inventory: IInventory) {
    val list = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
    for(i in 0 until inventory.sizeInventory)
        list[i] = inventory.getStackInSlot(i)
    ItemStackHelper.saveAllItems(compound, list)
}

fun loadInventory(compound: CompoundNBT, inventory: IInventory) {
    val list = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
    ItemStackHelper.loadAllItems(compound, list)
    for(i in 0 until inventory.sizeInventory)
        inventory.setInventorySlotContents(i, list[i])
}