package org.jglrxavpok.moarboats.common.modules

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import org.jglrxavpok.moarboats.common.state.StringBoatProperty

interface IFluidBoatModule {

    val fluidNameProperty: StringBoatProperty
    val fluidAmountProperty: IntBoatProperty

    fun getCapacity(boat: IControllable): Int
    fun canBeFilled(boat: IControllable): Boolean
    fun canBeDrained(boat: IControllable): Boolean

    fun canBeFilled(boat: IControllable, fluidStack: FluidStack): Boolean
    fun canBeDrained(boat: IControllable, fluidStack: FluidStack): Boolean

    fun getContents(boat: IControllable): FluidStack? {
        val fluid = getFluidInside(boat) ?: return null
        return FluidStack(fluid, fluidAmountProperty[boat])
    }

    fun getFluidInside(boat: IControllable): Fluid? {
        val name = fluidNameProperty[boat]
        return ForgeRegistries.FLUIDS.getValue(ResourceLocation(name))
    }

    fun getFluidAmount(boat: IControllable) = fluidAmountProperty[boat]

    fun fill(boat: IControllable, resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        val simulate = action.simulate()
        if(!canBeFilled(boat))
            return 0
        if(!canBeFilled(boat, resource))
            return 0
        val fluid = getFluidInside(boat)
        if(fluid != resource.fluid && getFluidAmount(boat) > 0) {
            return 0
        }
        fluidNameProperty[boat] = resource.fluid.registryName.toString()
        val filled = minOf(resource.amount, getCapacity(boat)-getFluidAmount(boat))
        if(!simulate) {
            fluidAmountProperty[boat] += filled
        }
        return filled
    }

    fun drain(boat: IControllable, resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack? {
        if(!canBeDrained(boat))
            return null
        if(!canBeDrained(boat, resource))
            return null
        val fluid = getFluidInside(boat) ?: return null // nothing to drain
        if(resource.fluid.registryName == fluid.registryName) {
            return drain(boat, resource.amount, action)
        }
        return null
    }

    fun drain(boat: IControllable, amount: Int, action: IFluidHandler.FluidAction): FluidStack? {
        if(!canBeDrained(boat))
            return null
        val fluid = getFluidInside(boat) ?: return null // nothing to drain
        val drained = minOf(amount, getFluidAmount(boat))
        if(action.execute()) {
            fluidAmountProperty[boat] -= drained
        }
        return FluidStack(fluid, drained)
    }

}