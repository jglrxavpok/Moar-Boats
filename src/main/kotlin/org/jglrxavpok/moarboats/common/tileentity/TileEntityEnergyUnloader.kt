package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.entity.Entity
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.util.Direction
import net.minecraftforge.energy.CapabilityEnergy
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing
import kotlin.math.ceil

class TileEntityEnergyUnloader: TileEntityEnergy(MoarBoats.TileEntityEnergyUnloaderType), ITickableTileEntity {
    override val maxReceivableEnergy = 0
    override val maxExtractableEnergy = maxEnergyStored

    private var working: Boolean = false
    val blockFacing: Direction get()= level!!.getBlockState(blockPos).getValue(Facing)

    override fun tick() {
        if(level!!.isClientSide)
            return
        working = false
        updateListeners()

        val facings = Direction.values().toMutableList()
        facings.remove(blockFacing)
        pushEnergyToNeighbors(MoarBoatsConfig.energyUnloader.sendAmount.get(), facings)

        val aabb = create3x3AxisAlignedBB(blockPos.relative(blockFacing))
        val entities = level!!.getEntitiesWithinAABB(Entity::class.java, aabb) { e -> e != null && e.getCapability(CapabilityEnergy.ENERGY, null).isPresent }

        val totalEnergyToPull = minOf(MoarBoatsConfig.energyUnloader.pullAmount.get(), maxEnergyStored-energyStored)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val energyToExtractFromASingleNeighbor = ceil(totalEnergyToPull.toDouble()/entityCount).toInt()
        var energyActuallyReceived = 0
        entities.forEach {
            val energyCapa = it.getCapability(CapabilityEnergy.ENERGY, null)
            energyCapa.ifPresent { storage ->
                energyActuallyReceived += storage.extractEnergy(energyToExtractFromASingleNeighbor, false)
                working = working || energyActuallyReceived > 0
            }
        }
        energy += energyActuallyReceived
        setChanged()
    }

    override fun getRedstonePower(): Int {
        return if(working) {
            val ratio = 1.0-(energyStored.toDouble()/maxEnergyStored) // signal is strongest when the buffer is empty (transfer almost finished)
            val redstonePower = (ratio * 15).toInt()
            minOf(1, redstonePower) // give a signal of at least 1 if currently working
        } else {
            0
        }
    }

    override fun isEnergyFacing(facing: Direction?): Boolean {
        return facing != blockFacing
    }

    override fun canExtract() = true

    override fun getMaxEnergyStored() = MoarBoatsConfig.energyUnloader.maxEnergy.get()

    override fun canReceive() = false
}