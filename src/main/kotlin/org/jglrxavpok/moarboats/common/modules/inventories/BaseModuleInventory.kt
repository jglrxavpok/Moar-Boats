package org.jglrxavpok.moarboats.common.modules.inventories

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.IIntArray
import net.minecraft.util.IntArray
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.api.IControllable

abstract class BaseModuleInventory(slotCount: Int, inventoryName: String, boat: IControllable, module: BoatModule):
        BoatModuleInventory(inventoryName, slotCount, boat, module, NonNullList.withSize(slotCount, ItemStack.EMPTY)) {

    override fun markDirty() {
    }

    override fun getStackInSlot(index: Int): ItemStack {
        return list[index]
    }

    override fun decrStackSize(index: Int, count: Int) = ItemStackHelper.getAndSplit(list, index, count)

    override fun clear() {
        list.clear()
    }

    override fun getSizeInventory() = list.size

    override fun isEmpty(): Boolean {
        return list.all { it.isEmpty }
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun getInventoryStackLimit() = 64

    override fun isUsableByPlayer(player: PlayerEntity): Boolean {
        return true
    }

    override fun openInventory(player: PlayerEntity?) {

    }

    override fun closeInventory(player: PlayerEntity?) {

    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        list[index] = stack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        return ItemStackHelper.getAndRemove(list, index)
    }

}