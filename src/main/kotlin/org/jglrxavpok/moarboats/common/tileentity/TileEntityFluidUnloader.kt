package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing

class TileEntityFluidUnloader: TileEntityListenable(), ITickable, IFluidHandler, IFluidTankProperties {

    val blockFacing: EnumFacing get()= world.getBlockState(pos).getValue(Facing)
    private var fluid: Fluid? = null
    private var fluidAmount: Int = 0
    private var working: Boolean = false

    override fun update() {
        if(world.isRemote)
            return
        working = false
        updateListeners()

        val aabb = create3x3AxisAlignedBB(pos.offset(blockFacing))
        val entities = world.getEntitiesWithinAABB(Entity::class.java, aabb) { e -> e != null && e.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) }

        val totalFluidToExtract = minOf(MoarBoatsConfig.fluidUnloader.pullAmount, capacity-fluidAmount)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val fluidToExtractFromASingleNeighbor = Math.ceil(totalFluidToExtract.toDouble()/entityCount).toInt()
        entities.forEach {
            val fluidCapa = it.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
            if(fluidCapa != null) {
                val amountFilled = if(fluid != null) {
                    forceFill(fluidCapa.drain(FluidStack(fluid, fluidToExtractFromASingleNeighbor), true) ?: FluidStack(fluid, 0), true)
                } else {
                    forceFill(fluidCapa.drain(fluidToExtractFromASingleNeighbor, true) ?: FluidStack(FluidRegistry.WATER, 0), true)
                }
                working = working || amountFilled > 0
            }
        }
    }

    override fun getRedstonePower(): Int {
        return if(working) {
            val ratio = 1.0-(fluidAmount.toDouble()/capacity) // signal is strongest when the buffer is empty (transfer almost finished)
            val redstonePower = (ratio * 15).toInt()
            minOf(1, redstonePower) // give a signal of at least 1 if currently working
        } else {
            0
        }
    }

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        if(!canDrain())
            return null
        if(!canDrainFluidType(resource))
            return null
        if(resource.fluid != fluid && resource.fluid != null)
            return null
        return drain(resource.amount, doDrain)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if(!canDrain())
            return null

        if(fluid == null)
            return null
        val maxDrainable = minOf(maxDrain, fluidAmount)
        if(doDrain) {
            fluidAmount -= maxDrainable
            markDirty()
        }
        return FluidStack(fluid, maxDrainable)
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        if(!canFill())
            return 0
        if(!canFillFluidType(resource))
            return 0
        return forceFill(resource, doFill)
    }

    fun forceFill(resource: FluidStack, doFill: Boolean): Int {
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
        return MoarBoatsConfig.fluidUnloader.capacity
    }

    override fun canFillFluidType(fluidStack: FluidStack?): Boolean {
        return false
    }

    override fun canFill(): Boolean {
        return false
    }

    override fun canDrainFluidType(fluidStack: FluidStack?): Boolean {
        return true
    }

    override fun canDrain(): Boolean {
        return true
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true
        return super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return this as T
        return super.getCapability(capability, facing)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("fluidAmount", fluidAmount)
        compound.setString("fluidName", fluid?.name ?: "")
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        fluid = FluidRegistry.getFluid(compound.getString("fluidName"))
        fluidAmount = compound.getInteger("fluidAmount")
    }

}