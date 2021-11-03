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

    override fun setChanged() {
    }

    override fun getItem(index: Int): ItemStack {
        return list[index]
    }

    override fun removeItem(index: Int, count: Int) = ItemStackHelper.removeItem(list, index, count)

    override fun clearContent() {
        list.clear()
    }

    override fun getContainerSize() = list.size

    override fun isEmpty(): Boolean {
        return list.all { it.isEmpty }
    }

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun getMaxStackSize() = 64

    override fun stillValid(player: PlayerEntity): Boolean {
        return true
    }

    override fun startOpen(player: PlayerEntity?) {
        
    }

    override fun stopOpen(player: PlayerEntity?) {

    }

    override fun setItem(index: Int, stack: ItemStack) {
        list[index] = stack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        return ItemStackHelper.takeItem(list, index)
    }

}