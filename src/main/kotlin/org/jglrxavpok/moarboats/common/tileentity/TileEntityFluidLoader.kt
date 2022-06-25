package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
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
import org.jglrxavpok.moarboats.common.BlockEntities
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.Facing
import kotlin.math.ceil

class TileEntityFluidLoader(blockPos: BlockPos, blockState: BlockState): FluidBlockEntity<TileEntityFluidLoader>(BlockEntities.FluidLoader.get(), blockPos, blockState) {

    override fun handleEntitiesInFront(entities: List<Pair<Entity, IFluidHandler>>) {
        val totalFluidToSend = minOf(MoarBoatsConfig.fluidLoader.sendAmount.get(), amount)
        val fluidToSendToASingleNeighbor = ceil(totalFluidToSend.toDouble()/entities.size).toInt()
        entities.forEach {
            val fluidCapa = it.second
            val amountDrained = forceDrain(fluidCapa.fill(FluidStack(fluid, fluidToSendToASingleNeighbor), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)
            working = working || amountDrained.amount > 0
        }
    }

    override fun getCapacity(): Int {
        return MoarBoatsConfig.fluidLoader.capacity.get()
    }

}