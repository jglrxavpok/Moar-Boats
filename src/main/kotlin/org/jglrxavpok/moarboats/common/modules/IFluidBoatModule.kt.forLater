package org.jglrxavpok.moarboats.common.modules

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
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
        return FluidRegistry.getFluid(name)
    }

    fun getFluidAmount(boat: IControllable) = fluidAmountProperty[boat]

    fun fill(boat: IControllable, resource: FluidStack, simulate: Boolean): Int {
        if(!canBeFilled(boat))
            return 0
        if(!canBeFilled(boat, resource))
            return 0
        val fluid = getFluidInside(boat)
        if(fluid != resource.fluid && getFluidAmount(boat) > 0) {
            return 0
        }
        fluidNameProperty[boat] = resource.fluid.name
        val filled = minOf(resource.amount, getCapacity(boat)-getFluidAmount(boat))
        if(!simulate) {
            fluidAmountProperty[boat] += filled
        }
        return filled
    }

    fun drain(boat: IControllable, resource: FluidStack, simulate: Boolean): FluidStack? {
        if(!canBeDrained(boat))
            return null
        if(!canBeDrained(boat, resource))
            return null
        val fluid = getFluidInside(boat) ?: return null // nothing to drain
        if(resource.fluid.name == fluid.name) {
            return drain(boat, resource.amount, simulate)
        }
        return null
    }

    fun drain(boat: IControllable, amount: Int, simulate: Boolean): FluidStack? {
        if(!canBeDrained(boat))
            return null
        val fluid = getFluidInside(boat) ?: return null // nothing to drain
        val drained = minOf(amount, getFluidAmount(boat))
        if(!simulate) {
            fluidAmountProperty[boat] -= drained
        }
        return FluidStack(fluid, drained)
    }

}