package org.jglrxavpok.moarboats.api

import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.*

interface IControllable {

    val entityID: Int
    val modules: List<BoatModule>
    val worldRef: World
    val positionX: Double
    val positionY: Double
    val positionZ: Double
    val velocityX: Double
    val velocityY: Double
    val velocityZ: Double
    val yaw: Float
    val correspondingEntity: Entity
    val moduleRNG: Random

    fun inWater(): Boolean

    fun turnRight(multiplier: Float = 1f)

    fun turnLeft(multiplier: Float = 1f)
    fun accelerate(multiplier: Float = 1f)
    fun decelerate(multiplier: Float = 1f)
    fun blockMovement()

    fun saveState(module: BoatModule)
    fun getState(module: BoatModule): NBTTagCompound
    fun getInventory(module: BoatModule): IBoatModuleInventory
}