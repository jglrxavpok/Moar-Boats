package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.energy.CapabilityEnergy
import org.jglrxavpok.moarboats.common.MBConfig
import org.jglrxavpok.moarboats.common.blocks.BlockEnergyLoader
import org.jglrxavpok.moarboats.common.blocks.Facing

class TileEntityEnergyLoader: TileEntityEnergy(), ITickable {
    override val maxReceivableEnergy = maxEnergyStored
    override val maxExtractableEnergy = 0

    val blockFacing: EnumFacing get()= world.getBlockState(pos).getValue(Facing)

    override fun update() {
        if(world.isRemote)
            return
        updateListeners()

        val facings = EnumFacing.values().toMutableList()
        facings.remove(blockFacing)
        pullEnergyFromNeighbors(MBConfig.energyLoaderPullAmount, facings)

        val aabb = AxisAlignedBB(pos.offset(blockFacing))
        val entities = world.getEntitiesWithinAABB(Entity::class.java, aabb) { e -> e != null && e.hasCapability(CapabilityEnergy.ENERGY, null) }

        val totalEnergyToSend = minOf(MBConfig.energyLoaderSendAmount, energyStored)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val energyToSendToASingleNeighbor = Math.ceil(totalEnergyToSend.toDouble()/entityCount).toInt()
        var energyActuallySent = 0
        entities.forEach {
            val energyCapa = it.getCapability(CapabilityEnergy.ENERGY, null)
            if(energyCapa != null) {
                energyActuallySent += energyCapa.receiveEnergy(energyToSendToASingleNeighbor, false)
            }
        }
        energy -= energyActuallySent
        markDirty()
    }

    override fun isEnergyFacing(facing: EnumFacing?): Boolean {
        return facing != blockFacing
    }

    override fun canExtract() = false

    override fun getMaxEnergyStored() = MBConfig.energyLoaderMaxEnergy

    override fun canReceive() = true
}