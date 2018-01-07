package org.jglrxavpok.moarboats.modules

import net.minecraft.inventory.IInventory
import net.minecraft.nbt.NBTTagCompound

interface IBoatModuleInventory: IInventory {

    val boat: IControllable
    val module: BoatModule

    fun getModuleState() = boat.getState(module)
    fun saveModuleState() {
        boat.saveState(module)
    }

}