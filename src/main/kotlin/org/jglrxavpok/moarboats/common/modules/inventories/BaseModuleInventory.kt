package org.jglrxavpok.moarboats.common.modules.inventories

import net.minecraft.core.NonNullList
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
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

    override fun removeItem(index: Int, count: Int) = ContainerHelper.removeItem(list, index, count)

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

    override fun stillValid(player: Player): Boolean {
        return true
    }

    override fun startOpen(player: Player?) {
        
    }

    override fun stopOpen(player: Player?) {

    }

    override fun setItem(index: Int, stack: ItemStack) {
        list[index] = stack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        return ContainerHelper.takeItem(list, index)
    }

}