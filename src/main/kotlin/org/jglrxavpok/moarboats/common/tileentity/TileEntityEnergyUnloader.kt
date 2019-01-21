package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.energy.CapabilityEnergy
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing

class TileEntityEnergyUnloader: TileEntityEnergy(), ITickable {
    override val maxReceivableEnergy = 0
    override val maxExtractableEnergy = maxEnergyStored

    val blockFacing: EnumFacing get()= world.getBlockState(pos).getValue(Facing)

    override fun update() {
        if(world.isRemote)
            return
        updateListeners()

        val facings = EnumFacing.values().toMutableList()
        facings.remove(blockFacing)
        pushEnergyToNeighbors(MoarBoatsConfig.energyUnloader.sendAmount, facings)

        val aabb = AxisAlignedBB(pos.offset(blockFacing))
        val entities = world.getEntitiesWithinAABB(Entity::class.java, aabb) { e -> e != null && e.hasCapability(CapabilityEnergy.ENERGY, null) }

        val totalEnergyToPull = minOf(MoarBoatsConfig.energyUnloader.pullAmount, maxEnergyStored-energyStored)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val energyToExtractFromASingleNeighbor = Math.ceil(totalEnergyToPull.toDouble()/entityCount).toInt()
        var energyActuallyReceived = 0
        entities.forEach {
            val energyCapa = it.getCapability(CapabilityEnergy.ENERGY, null)
            if(energyCapa != null) {
                energyActuallyReceived += energyCapa.extractEnergy(energyToExtractFromASingleNeighbor, false)
            }
        }
        energy += energyActuallyReceived
        markDirty()
    }

    override fun isEnergyFacing(facing: EnumFacing?): Boolean {
        return facing != blockFacing
    }

    override fun canExtract() = true

    override fun getMaxEnergyStored() = MoarBoatsConfig.energyUnloader.maxEnergy

    override fun canReceive() = false
}