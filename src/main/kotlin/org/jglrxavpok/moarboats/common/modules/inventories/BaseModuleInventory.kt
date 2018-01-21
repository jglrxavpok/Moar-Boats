package org.jglrxavpok.moarboats.common.modules.inventories

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IBoatModuleInventory
import org.jglrxavpok.moarboats.api.IControllable

abstract class BaseModuleInventory(val slotCount: Int, val inventoryName: String, override val boat: IControllable, override val module: BoatModule): InventoryBasic(inventoryName,  true,slotCount), IBoatModuleInventory {

    override val list = NonNullList.withSize(slotCount, ItemStack.EMPTY)

    protected abstract fun id2key(id: Int): String?
    abstract override fun getFieldCount(): Int

    override fun getField(id: Int): Int {
        val key = id2key(id)
        if(key != null)
            return getModuleState().getInteger(key)
        return -1
    }

    override fun hasCustomName(): Boolean {
        return false
    }

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

    override fun getName() = inventoryName

    override fun isEmpty(): Boolean {
        return list.all { it.isEmpty }
    }

    override fun getDisplayName(): ITextComponent {
        return TextComponentTranslation("inventory.$inventoryName.name")
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun getInventoryStackLimit() = 64

    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return true
    }

    override fun openInventory(player: EntityPlayer?) {

    }

    override fun setField(id: Int, value: Int) {
        val key = id2key(id)
        if(key != null) {
            getModuleState().setInteger(key, value)
            saveModuleState()
        }
    }

    override fun closeInventory(player: EntityPlayer?) {

    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        list[index] = stack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        return ItemStackHelper.getAndRemove(list, index)
    }

}