package org.jglrxavpok.moarboats.common.modules

import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import kotlin.math.min

interface IEnergyBoatModule {
    val energyProperty: IntBoatProperty
    fun canReceiveEnergy(boat: IControllable): Boolean
    fun canGiveEnergy(boat: IControllable): Boolean
    fun getMaxStorableEnergy(boat: IControllable): Int

    fun receiveEnergy(boat: IControllable, amount: Int, simulate: Boolean): Int {
        if(!canReceiveEnergy(boat))
            return 0
        val receivable = min(getMaxStorableEnergy(boat)-getCurrentEnergy(boat), amount)
        if(!simulate) {
            energyProperty[boat] += receivable
        }
        return receivable
    }

    fun extractEnergy(boat: IControllable, amount: Int, simulate: Boolean): Int {
        if(!canGiveEnergy(boat))
            return 0
        val extractable = min(getCurrentEnergy(boat), amount)
        if(!simulate) {
            energyProperty[boat] -= extractable
        }
        return extractable
    }

    fun getCurrentEnergy(boat: IControllable): Int {
        return energyProperty[boat]
    }
}