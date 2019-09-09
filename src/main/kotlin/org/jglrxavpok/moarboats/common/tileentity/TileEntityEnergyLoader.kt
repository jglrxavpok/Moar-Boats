package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.entity.Entity
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.util.Direction
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.energy.CapabilityEnergy
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing
import kotlin.math.ceil

class TileEntityEnergyLoader: TileEntityEnergy(MoarBoats.TileEntityEnergyLoaderType), ITickable {
    override val maxReceivableEnergy = maxEnergyStored
    override val maxExtractableEnergy = 0
    private var working: Boolean = false

    val blockFacing: Direction get()= level!!.getBlockState(pos).get(Facing)

    override fun tick() {
        if(level!!.isClientSide)
            return
        working = false
        updateListeners()

        val facings = Direction.values().toMutableList()
        facings.remove(blockFacing)
        pullEnergyFromNeighbors(MoarBoatsConfig.energyLoader.pullAmount.get(), facings)

        val aabb = create3x3AxisAlignedBB(pos.offset(blockFacing))
        val entities = level!!.getEntitiesWithinAABB(Entity::class.java, aabb) { e -> e != null && e.getCapability(CapabilityEnergy.ENERGY, null).isPresent }

        val totalEnergyToSend = minOf(MoarBoatsConfig.energyLoader.sendAmount.get(), energyStored)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val energyToSendToASingleNeighbor = ceil(totalEnergyToSend.toDouble()/entityCount).toInt()
        var energyActuallySent = 0
        entities.forEach {
            val energyCapa = it.getCapability(CapabilityEnergy.ENERGY, null)
            energyCapa.ifPresent { storage ->
                energyActuallySent += storage.receiveEnergy(energyToSendToASingleNeighbor, false)
                working = working || energyActuallySent > 0
            }
        }
        energy -= energyActuallySent
        setChanged()
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

    override fun isEnergyFacing(facing: Direction?): Boolean {
        return facing != blockFacing
    }

    override fun canExtract() = false

    override fun getMaxEnergyStored() = MoarBoatsConfig.energyLoader.maxEnergy.get()

    override fun canReceive() = true
}