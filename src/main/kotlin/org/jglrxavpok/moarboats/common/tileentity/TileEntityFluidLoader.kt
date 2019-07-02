package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing

class TileEntityFluidLoader: TileEntityListenable(MoarBoats.TileEntityFluidLoader), ITickable, IFluidHandler, IFluidTankProperties {

    val blockFacing: EnumFacing get()= world.getBlockState(pos).get(Facing)
    private var fluid: Fluid? = null
    private var fluidAmount: Int = 0
    private var working: Boolean = false

    override fun tick() {
        if(world.isRemote)
            return
        working = false
        updateListeners()

        if(fluid == null)
            return

        val aabb = create3x3AxisAlignedBB(pos.offset(blockFacing))
        val entities = world.getEntitiesWithinAABB(Entity::class.java, aabb) { e -> e != null && e.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent }

        val totalFluidToSend = minOf(MoarBoatsConfig.fluidLoader.sendAmount, fluidAmount)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val fluidToSendToASingleNeighbor = Math.ceil(totalFluidToSend.toDouble()/entityCount).toInt()
        entities.forEach {
            val fluidCapa = it.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
            fluidCapa.ifPresent { storage ->
                val amountDrained = forceDrain(storage.fill(FluidStack(fluid, fluidToSendToASingleNeighbor), true), true)
                working = working || (amountDrained?.amount ?: 0) > 0
            }
        }
    }

    override fun getRedstonePower(): Int {
        return if(working) {
            val ratio = fluidAmount.toDouble()/capacity // signal is strongest when the buffer is full (transfer almost finished)
            val redstonePower = (ratio * 15).toInt()
            minOf(1, redstonePower) // give a signal of at least 1 if currently working
        } else {
            0
        }
    }

    fun forceDrain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if(fluid == null)
            return null
        val maxDrainable = minOf(maxDrain, fluidAmount)
        if(doDrain) {
            fluidAmount -= maxDrainable
            markDirty()
        }
        return FluidStack(fluid, maxDrainable)
    }

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        if(!canDrain())
            return null
        if(!canDrainFluidType(resource))
            return null
        if(resource.fluid != fluid)
            return null
        return drain(resource.amount, doDrain)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if(!canDrain())
            return null
        return forceDrain(maxDrain, doDrain)
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        if(!canFill())
            return 0
        if(!canFillFluidType(resource))
            return 0
        if(fluid == null) {
            fluid = resource.fluid
        }
        if(fluid != resource.fluid && fluidAmount > 0) {
            return 0
        }
        fluid = resource.fluid
        val maxFillable = minOf(resource.amount, capacity-fluidAmount)
        if(doFill) {
            fluidAmount += maxFillable
            markDirty()
        }
        return maxFillable
    }

    override fun getTankProperties(): Array<IFluidTankProperties> = arrayOf(this)

    override fun getContents(): FluidStack? {
        if(fluid == null || fluidAmount == 0)
            return null
        return FluidStack(fluid, fluidAmount)
    }

    override fun getCapacity(): Int {
        return MoarBoatsConfig.fluidLoader.capacity
    }

    override fun canFillFluidType(fluidStack: FluidStack?): Boolean {
        return true
    }

    override fun canFill(): Boolean {
        return true
    }

    override fun canDrainFluidType(fluidStack: FluidStack?): Boolean {
        return false
    }

    override fun canDrain(): Boolean {
        return false
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): LazyOptional<T> {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of { this }.cast()
        return super.getCapability(capability, facing)
    }

    override fun write(compound: NBTTagCompound): NBTTagCompound {
        compound.setInt("fluidAmount", fluidAmount)
        compound.setString("fluidName", fluid?.name ?: "")
        return super.write(compound)
    }

    override fun read(compound: NBTTagCompound) {
        super.read(compound)
// FIXME        fluid = FluidRegistry.getFluid(compound.getString("fluidName"))
        fluidAmount = compound.getInt("fluidAmount")
    }

}