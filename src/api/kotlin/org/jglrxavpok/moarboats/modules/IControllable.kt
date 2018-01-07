package org.jglrxavpok.moarboats.modules

import net.minecraft.inventory.IInventory
import net.minecraft.nbt.NBTTagCompound

interface IControllable {

    val entityID: Int

    fun turnRight(multiplier: Float = 1f)
    fun turnLeft(multiplier: Float = 1f)
    fun accelerate(multiplier: Float = 1f)

    fun decelerate(multiplier: Float = 1f)
    fun saveState(module: BoatModule)
    fun getState(module: BoatModule): NBTTagCompound

    fun getInventory(module: BoatModule): IBoatModuleInventory
}