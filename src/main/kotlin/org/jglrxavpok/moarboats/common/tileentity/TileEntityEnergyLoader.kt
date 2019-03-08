package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.entity.Entity
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.energy.CapabilityEnergy
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing

class TileEntityEnergyLoader: TileEntityEnergy(), ITickable {
    override val maxReceivableEnergy = maxEnergyStored
    override val maxExtractableEnergy = 0
    private var working: Boolean = false

    val blockFacing: EnumFacing get()= world.getBlockState(pos).getValue(Facing)

    override fun update() {
        if(world.isRemote)
            return
        working = false
        updateListeners()

        val facings = EnumFacing.values().toMutableList()
        facings.remove(blockFacing)
        pullEnergyFromNeighbors(MoarBoatsConfig.energyLoader.pullAmount, facings)

        val aabb = create3x3AxisAlignedBB(pos.offset(blockFacing))
        val entities = world.getEntitiesWithinAABB(Entity::class.java, aabb) { e -> e != null && e.hasCapability(CapabilityEnergy.ENERGY, null) }

        val totalEnergyToSend = minOf(MoarBoatsConfig.energyLoader.sendAmount, energyStored)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val energyToSendToASingleNeighbor = Math.ceil(totalEnergyToSend.toDouble()/entityCount).toInt()
        var energyActuallySent = 0
        entities.forEach {
            val energyCapa = it.getCapability(CapabilityEnergy.ENERGY, null)
            if(energyCapa != null) {
                energyActuallySent += energyCapa.receiveEnergy(energyToSendToASingleNeighbor, false)
                working = working || energyActuallySent > 0
            }
        }
        energy -= energyActuallySent
        markDirty()
    }

    override fun getRedstonePower(): Int {
        return if(working) {
            val ratio = energyStored.toDouble()/maxEnergyStored // signal is strongest when the buffer is full (transfer almost finished)
            val redstonePower = (ratio * 15).toInt()
            minOf(1, redstonePower) // give a signal of at least 1 if currently working
        } else {
            0
        }
    }

    override fun isEnergyFacing(facing: EnumFacing?): Boolean {
        return facing != blockFacing
    }

    override fun canExtract() = false

    override fun getMaxEnergyStored() = MoarBoatsConfig.energyLoader.maxEnergy

    override fun canReceive() = true
}