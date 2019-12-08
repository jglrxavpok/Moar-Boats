package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.IContainerListener
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import java.lang.reflect.Field

private lateinit var listenersField: Field

fun getListeners(container: Container): List<IContainerListener> {
    if(!::listenersField.isInitialized) {
        listenersField = ObfuscationReflectionHelper.findField(Container::class.java, "field_75149_d")
    }
    return listenersField[container] as List<IContainerListener>
}

abstract class ContainerBase<T: Container>(val containerRef: ContainerType<T>, val containerID: Int, val playerInventory: PlayerInventory): Container(containerRef, containerID) {

    protected fun addPlayerSlots(isLarge: Boolean, xStart: Int = 8) {
        val yOffset = if(isLarge) 3 * 18 +2 else 0
        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(playerInventory, j + i * 9 + 9, xStart + j * 18, 84 + i * 18 + yOffset))
            }
        }

        for (k in 0..8) {
            this.addSlot(Slot(playerInventory, k, xStart + k * 18, 142 + yOffset))
        }
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index in 0..27) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return ItemStack.EMPTY
                }
            } else if (index in 27..35 && !this.mergeItemStack(itemstack1, 0, 26, false)) {
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
}