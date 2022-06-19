package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.extensions.IForgeBlockEntity
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.BlockEntities
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing

class TileEntityFluidUnloader(blockPos: BlockPos, blockState: BlockState): TileEntityListenable(BlockEntities.FluidUnloader.get(), blockPos, blockState), ITickableTileEntity, IFluidHandler, IFluidTank,
    IForgeBlockEntity {
    val blockFacing: Direction get()= level!!.getBlockState(blockPos).getValue(Facing)
    private var fluid: Fluid? = null
    private var amount: Int = 0
    private var working: Boolean = false

    override fun tick() {
        if(level!!.isClientSide)
            return
        working = false
        updateListeners()

        val aabb = create3x3AxisAlignedBB(blockPos.relative(blockFacing))
        val entities = level!!.getEntitiesOfClass(Entity::class.java, aabb) { e -> e != null && e.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent }

        val totalFluidToExtract = minOf(MoarBoatsConfig.fluidUnloader.pullAmount.get(), getCapacity()-amount)
        val entityCount = entities.size
        if(entityCount <= 0)
            return
        val fluidToExtractFromASingleNeighbor = Math.ceil(totalFluidToExtract.toDouble()/entityCount).toInt()
        entities.forEach {
            val fluidCapa = it.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
            fluidCapa.ifPresent { storage ->
                val amountFilled = if(fluid != null) {
                    forceFill(storage.drain(FluidStack(fluid, fluidToExtractFromASingleNeighbor), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)
                } else {
                    forceFill(storage.drain(fluidToExtractFromASingleNeighbor, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)
                }
                working = working || amountFilled > 0
            }
        }
    }

    override fun getRedstonePower(): Int {
        return if(working) {
            val ratio = 1.0-(amount.toDouble()/getCapacity()) // signal is strongest when the buffer is empty (transfer almost finished)
            val redstonePower = (ratio * 15).toInt()
            minOf(1, redstonePower) // give a signal of at least 1 if currently working
        } else {
            0
        }
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        if(!canDrain())
            return FluidStack.EMPTY
        if(!canDrainFluidType(resource))
            return FluidStack.EMPTY
        if(resource.fluid != fluid && resource.fluid != null)
            return FluidStack.EMPTY
        return drain(resource.amount, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        if(!canDrain())
            return FluidStack.EMPTY

        if(fluid == null)
            return FluidStack.EMPTY
        val maxDrainable = minOf(maxDrain, amount)
        if(action.execute()) {
            amount -= maxDrainable
            setChanged()
        }
        return FluidStack(fluid, maxDrainable)
    }

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        if(!canFill())
            return 0
        if(!canFillFluidType(resource))
            return 0
        return forceFill(resource, action)
    }

    fun forceFill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        if(fluid == null) {
            fluid = resource.fluid
        }
        if(fluid != resource.fluid && amount > 0) {
            return 0
        }
        fluid = resource.fluid
        val maxFillable = minOf(resource.amount, getCapacity()-amount)
        if(action.execute()) {
            amount += maxFillable
            setChanged()
        }
        return maxFillable
    }

    override fun getCapacity(): Int {
        return MoarBoatsConfig.fluidUnloader.capacity.get()
    }

    fun canFillFluidType(fluidStack: FluidStack?): Boolean {
        return false
    }

    fun canFill(): Boolean {
        return false
    }

    fun canDrainFluidType(fluidStack: FluidStack?): Boolean {
        return true
    }

    fun canDrain(): Boolean {
        return true
    }

    override fun getTankCapacity(tank: Int): Int {
        return if(tank == 0) capacity else 0
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        return if(tank == 0) getFluid() else FluidStack.EMPTY
    }

    override fun getTanks(): Int {
        return 1
    }

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean {
        return true
    }

    override fun isFluidValid(stack: FluidStack?): Boolean {
        return true
    }

    override fun getFluidAmount(): Int {
        return amount
    }

    override fun getFluid(): FluidStack {
        return if(fluid != null) FluidStack(fluid, amount) else FluidStack.EMPTY
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: Direction?): LazyOptional<T> {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of { this }.cast()
        return super<TileEntityListenable>.getCapability(capability, facing)
    }

    override fun saveAdditional(compound: CompoundTag) {
        super.saveAdditional(compound)
        compound.putInt("fluidAmount", amount)
        compound.putString("fluidName", fluid?.let { ForgeRegistries.FLUIDS.getKey(it)?.toString() } ?: "")
    }

    override fun load(compound: CompoundTag) {
        super.load(compound)
        fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation(compound.getString("fluidName")))
        amount = compound.getInt("fluidAmount")
    }

}