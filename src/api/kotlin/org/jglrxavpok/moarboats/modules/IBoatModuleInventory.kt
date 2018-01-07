package org.jglrxavpok.moarboats.modules

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

interface IBoatModuleInventory: IInventory {

    val boat: IControllable
    val module: BoatModule
    val list: NonNullList<ItemStack>

    fun getModuleState() = boat.getState(module)
    fun saveModuleState() {
        boat.saveState(module)
    }

}