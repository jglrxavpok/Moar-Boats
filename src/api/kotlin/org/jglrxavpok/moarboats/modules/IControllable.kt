package org.jglrxavpok.moarboats.modules

import net.minecraft.nbt.NBTTagCompound

interface IControllable {

    fun turnRight()
    fun turnLeft()
    fun accelerate()
    fun decelerate()

    fun saveState(module: BoatModule)
    fun getState(module: BoatModule): NBTTagCompound

}