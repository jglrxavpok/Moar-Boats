package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity
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

class TileEntityFluidUnloader(blockPos: BlockPos, blockState: BlockState): FluidBlockEntity<TileEntityFluidUnloader>(BlockEntities.FluidUnloader.get(), blockPos, blockState) {

    override fun handleEntitiesInFront(entities: List<Pair<Entity, IFluidHandler>>) {
        val totalFluidToExtract = minOf(MoarBoatsConfig.fluidUnloader.pullAmount.get(), capacity - amount)
        val fluidToExtractFromASingleNeighbor = Math.ceil(totalFluidToExtract.toDouble()/entities.size).toInt()
        entities.forEach {
            val fluidCapa = it.second
            val amountFilled = if(!isEmpty()) {
                forceFill(fluidCapa.drain(FluidStack(fluid, fluidToExtractFromASingleNeighbor), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)
            } else {
                forceFill(fluidCapa.drain(fluidToExtractFromASingleNeighbor, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)
            }
            working = working || amountFilled > 0
        }
    }

    override fun getCapacity(): Int {
        return MoarBoatsConfig.fluidUnloader.capacity.get()
    }
}