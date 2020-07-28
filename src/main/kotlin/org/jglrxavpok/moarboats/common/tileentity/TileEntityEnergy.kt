package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import java.lang.NullPointerException

/**
 * From ThunderScience https://github.com/jglrxavpok/ThunderScience/blob/master/src/main/kotlin/org/jglrxavpok/thunderscience/common/tileentity/TileEntityEnergy.kt
 */
abstract class TileEntityEnergy(tileEntityType: TileEntityType<out TileEntityEnergy>): TileEntityListenable(tileEntityType), IEnergyStorage {

    internal var energy: Int = 0
    protected abstract val maxReceivableEnergy: Int
    protected abstract val maxExtractableEnergy: Int

    override fun deserializeNBT(state: BlockState, compound: CompoundNBT) {
        super.deserializeNBT(state, compound)
        energy = compound.getInt("energy")
    }

    override fun write(compound: CompoundNBT): CompoundNBT {
        compound.putInt("energy", energy)
        return super.write(compound)
    }

    override fun getEnergyStored(): Int {
        return energy
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        if(!canExtract())
            return 0
        val maxExtracted = Math.min(energy, Math.min(maxExtract, maxExtractableEnergy))
        val newEnergyLevel = energy - maxExtracted
        val actuallyExtracted = energy - newEnergyLevel
        if(!simulate) {
            energy -= maxExtracted
            markDirty()
        }
        return actuallyExtracted
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        if(!canReceive())
            return 0
        val maxExtracted = Math.min(maxEnergyStored-energy, Math.min(maxReceive, maxReceivableEnergy))
        val newEnergyLevel = energy + maxExtracted
        val actuallyReceived = newEnergyLevel - energy
        if(!simulate) {
            energy += maxExtracted
            markDirty()
        }
        return actuallyReceived
    }

    fun pushEnergyToNeighbors(totalEnergyToSend: Int, facings: List<Direction> = Direction.values().toList()) {
        val neighborCount = countNeighbors(facings, IEnergyStorage::canReceive)
        if(neighborCount <= 0)
            return
        val energyToSend = minOf(totalEnergyToSend, energy)
        val energyToSendToASingleNeighbor = Math.ceil(energyToSend.toDouble()/neighborCount).toInt()
        var energyActuallySent = 0
        neighborsThatCanReceivePower(facings, IEnergyStorage::canReceive).forEach {
            energyActuallySent += it.receiveEnergy(energyToSendToASingleNeighbor, false)
        }
        energy -= energyActuallySent
        markDirty()
    }

    fun pullEnergyFromNeighbors(totalEnergyToReceive: Int, facings: List<Direction> = Direction.values().toList()) {
        val neighborCount = countNeighbors(facings, IEnergyStorage::canExtract)
        if(neighborCount <= 0)
            return
        val energyToReceive = minOf(totalEnergyToReceive, maxEnergyStored-energy)
        val energyToReceiveFromASingleNeighbor = Math.ceil(energyToReceive.toDouble()/neighborCount).toInt()
        var energyActuallyReceived = 0
        neighborsThatCanReceivePower(facings, IEnergyStorage::canExtract).forEach {
            energyActuallyReceived += it.extractEnergy(energyToReceiveFromASingleNeighbor, false)
        }
        energy += energyActuallyReceived
        markDirty()
    }

    private fun neighborsThatCanReceivePower(facings: List<Direction> = Direction.values().toList(), powerFunction: (IEnergyStorage) -> Boolean) =
            facings
                    .map {
                        val neighborPos = pos.offset(it)
                        getPowerCapability(neighborPos, it.opposite)
                    }
                    .filter {it.isPresent}
                    .map { it.orElseThrow(::NullPointerException) }
                    .filter(powerFunction)

    private fun countNeighbors(facings: List<Direction> = Direction.values().toList(), powerFunction: (IEnergyStorage) -> Boolean): Int {
        return neighborsThatCanReceivePower(facings, powerFunction).count()
    }

    private fun getPowerCapability(pos: BlockPos, facing: Direction): LazyOptional<IEnergyStorage> {
        val te = world!!.getTileEntity(pos)
        if(te != null) {
            return te.getCapability(CapabilityEnergy.ENERGY)
        }
        return LazyOptional.empty<IEnergyStorage>()
    }

    fun consumeEnergy(amount: Int): Boolean {
        if(amount > energy)
            return false
        energy -= amount
        markDirty()
        return true
    }

    abstract fun isEnergyFacing(facing: Direction?): Boolean

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: Direction?): LazyOptional<T> {
        if(capability == CapabilityEnergy.ENERGY && isEnergyFacing(facing)) {
            return LazyOptional.of { this }.cast()
        }
        return super.getCapability(capability, facing)
    }

}