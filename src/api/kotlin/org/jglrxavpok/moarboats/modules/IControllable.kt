package org.jglrxavpok.moarboats.modules

import net.minecraft.nbt.NBTTagCompound

interface IControllable {

    fun turnRight(multiplier: Float = 1f)
    fun turnLeft(multiplier: Float = 1f)
    fun accelerate(multiplier: Float = 1f)
    fun decelerate(multiplier: Float = 1f)

    fun saveState(module: BoatModule)
    fun getState(module: BoatModule): NBTTagCompound

}