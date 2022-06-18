package org.jglrxavpok.moarboats.extensions

import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Container
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.ContainerHelper
import net.minecraft.world.item.ItemStack

fun saveInventory(compound: CompoundTag, inventory: Container) {
    val list = NonNullList.withSize(inventory.containerSize, ItemStack.EMPTY)
    for(i in 0 until inventory.containerSize)
        list[i] = inventory.getItem(i)
    ContainerHelper.saveAllItems(compound, list)
}

fun loadInventory(compound: CompoundTag, inventory: Container) {
    val list = NonNullList.withSize(inventory.containerSize, ItemStack.EMPTY)
    ContainerHelper.loadAllItems(compound, list)
    for(i in 0 until inventory.containerSize)
        inventory.setItem(i, list[i])
}