package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import org.jglrxavpok.moarboats.common.MBConfig
import org.jglrxavpok.moarboats.common.blocks.Facing

class TileEntityFluidLoader: TileEntityListenable(), ITickable, IFluidHandler, IFluidTankProperties {

    val blockFacing: EnumFacing get()= world.getBlockState(pos).getValue(Facing)
    private var fluid: Fluid? = null
    private var fluidAmount: Int = 0

    override fun update() {
        if(world.isRemote)
            return
        updateListeners()

        if(fluid == null)
            return

        val aabb = AxisAlignedBB(pos.offset(blockFacing))
        val entities = world.getEntitiesWithinAABB(Entity::class.java, aabb) { e -> e != null && e.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) }

        val totalFluidToSend = minOf(MBConfig.fluidLoaderSendAmount, fluidAmount)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val fluidToSendToASingleNeighbor = Math.ceil(totalFluidToSend.toDouble()/entityCount).toInt()
        entities.forEach {
            val fluidCapa = it.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
            if(fluidCapa != null) {
                forceDrain(fluidCapa.fill(FluidStack(fluid, fluidToSendToASingleNeighbor), true), true)
            }
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
        return MBConfig.fluidLoaderCapacity
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