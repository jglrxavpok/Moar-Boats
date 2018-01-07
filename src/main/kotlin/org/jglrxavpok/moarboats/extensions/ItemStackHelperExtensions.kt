package org.jglrxavpok.moarboats.extensions

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList

fun saveInventory(compound: NBTTagCompound, inventory: IInventory) {
    val list = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
    for(i in 0 until inventory.sizeInventory)
        list[i] = inventory.getStackInSlot(i)
    ItemStackHelper.saveAllItems(compound, list)
}

fun loadInventory(compound: NBTTagCompound, inventory: IInventory) {
    val list = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
    ItemStackHelper.loadAllItems(compound, list)
    for(i in 0 until inventory.sizeInventory)
        inventory.setInventorySlotContents(i, list[i])
}