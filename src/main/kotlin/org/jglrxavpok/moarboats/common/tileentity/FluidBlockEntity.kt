package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntityType
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
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing

abstract class FluidBlockEntity<T: TileEntityListenable>(entityType: BlockEntityType<T>, blockPos: BlockPos, blockState: BlockState): TileEntityListenable(entityType, blockPos, blockState), ITickableTileEntity, IFluidHandler, IFluidTank, IForgeBlockEntity {
    protected var fluid: Fluid? = null
    protected var amount: Int = 0
    protected var working: Boolean = false

    val blockFacing: Direction get()= level!!.getBlockState(blockPos).getValue(Facing)

    /**
     * Not called if no entities in front (ie entities is never empty)
     */
    abstract fun handleEntitiesInFront(entities: List<Pair<Entity, IFluidHandler>>)

    fun isEmpty() = fluid == null || amount == 0

    override fun tick() {
        if(level!!.isClientSide)
            return
        working = false
        updateListeners()

        val aabb = create3x3AxisAlignedBB(blockPos.relative(blockFacing))
        val entities = level!!.getEntitiesOfClass(Entity::class.java, aabb) { e -> e != null && e.getCapability(
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent }

        val entityCount = entities.size
        if(entityCount <= 0)
            return

        val toHandle = mutableListOf<Pair<Entity, IFluidHandler>>()
        entities.forEach { entity ->
            val fluidCapa = entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
            fluidCapa.ifPresent { storage ->
                toHandle.add(entity to storage)
            }
        }

        if(toHandle.size > 0)
            handleEntitiesInFront(toHandle)
    }

    override fun getRedstonePower(): Int {
        return if(working) {
            val ratio = amount.toDouble()/getCapacity() // signal is strongest when the buffer is full (transfer almost finished)
            val redstonePower = (ratio * 15).toInt()
            minOf(1, redstonePower) // give a signal of at least 1 if currently working
        } else {
            0
        }
    }

    fun forceDrain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        if(isEmpty())
            return FluidStack.EMPTY
        val maxDrainable = minOf(maxDrain, amount)
        if(action.execute()) {
            amount -= maxDrainable
            setChanged()
        }
        return FluidStack(fluid, maxDrainable)
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

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        if(!canDrain())
            return FluidStack.EMPTY
        if(!canDrainFluidType(resource))
            return FluidStack.EMPTY
        if(resource.fluid != fluid)
            return FluidStack.EMPTY
        return drain(resource.amount, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        if(!canDrain())
            return FluidStack.EMPTY
        return forceDrain(maxDrain, action)
    }

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        if(!canFill())
            return 0
        if(!canFillFluidType(resource))
            return 0
        if(fluid == null) {
            fluid = resource.fluid
        }
        if(fluid != resource.fluid && amount > 0) {
            return 0
        }
        val maxFillable = minOf(resource.amount, getCapacity()-amount)
        if(action.execute()) {
            fluid = resource.fluid
            amount += maxFillable
            setChanged()
        }
        return maxFillable
    }

    open fun canFillFluidType(fluidStack: FluidStack?): Boolean {
        return true
    }

    open fun canFill(): Boolean {
        return true
    }

    open fun canDrainFluidType(fluidStack: FluidStack?): Boolean {
        return true
    }

    open fun canDrain(): Boolean {
        return true
    }

    override fun getTankCapacity(tank: Int): Int {
        return if(tank == 0) capacity else 0
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        return if(fluid != null && tank == 0 && amount > 0) FluidStack(fluid, amount) else FluidStack.EMPTY
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
        return getFluidInTank(0)
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