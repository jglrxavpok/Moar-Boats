package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerListener
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.util.ObfuscationReflectionHelper
import java.lang.reflect.Field

private lateinit var listenersField: Field

fun getListeners(container: AbstractContainerMenu): List<ContainerListener> {
    if(!::listenersField.isInitialized) {
        listenersField = ObfuscationReflectionHelper.findField(AbstractContainerMenu::class.java, "field_75149_d")
    }
    return listenersField[container] as List<ContainerListener>
}

abstract class ContainerBase<T: AbstractContainerMenu>(val containerRef: MenuType<T>, val containerID: Int, val playerInventory: Inventory): AbstractContainerMenu(containerRef, containerID) {

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

    override fun stillValid(playerIn: Player): Boolean {
        return true
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.slots[index]

        if (slot != null && slot.hasItem()) {
            val itemstack1 = slot.item
            itemstack = itemstack1.copy()

            if (index in 0..27) {
                if (!this.moveItemStackTo(itemstack1, 27, 36, false)) {
                    return ItemStack.EMPTY
                }
            } else if (index in 27..35 && !this.moveItemStackTo(itemstack1, 0, 26, false)) {
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
}