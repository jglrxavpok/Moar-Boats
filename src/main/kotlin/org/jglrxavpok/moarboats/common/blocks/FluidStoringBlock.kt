package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.network.NetworkHooks
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.FluidBlockEntity
import org.jglrxavpok.moarboats.common.tileentity.TileEntityFluidLoader
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

abstract class FluidStoringBlock<T: FluidBlockEntity<T>>(): MoarBoatsBlockEntity() {

    abstract fun createInteration(blockPos: BlockPos): MoarBoatsGuiHandler.TileEntityInteraction<out BlockEntity>

    override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    override fun getAnalogOutputSignal(blockState: BlockState, worldIn: Level, pos: BlockPos): Int {
        return (worldIn.getBlockEntity(pos) as? FluidBlockEntity<TileEntityListenable>)?.getRedstonePower() ?: 0
    }

    override fun use(state: BlockState, worldIn: Level, pos: BlockPos, player: Player, handIn: InteractionHand, hit: BlockHitResult): InteractionResult {
        if(worldIn.isClientSide)
            return InteractionResult.SUCCESS
        val heldStack = player.getItemInHand(handIn)
        val capability = heldStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
        val fluidTransfer = capability.map {
            val te = worldIn.getBlockEntity(pos) as FluidBlockEntity<T>

            // try to fill
            val drainMode = if(player.isCreative) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
            var anyTransfer: Boolean =
                if(te.fluid.isEmpty) {
                    te.fill(it.drain(te.capacity, drainMode), IFluidHandler.FluidAction.EXECUTE) > 0
                } else {
                    val availableCapacity = te.capacity - te.fluidAmount
                    te.fill(it.drain(FluidStack(te.fluid, availableCapacity), drainMode), IFluidHandler.FluidAction.EXECUTE) > 0
                }
            if(!anyTransfer && !te.fluid.isEmpty){
                // try to drain
                val drainedAmount = it.fill(te.drain(te.capacity, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE)
                anyTransfer = it.fill(te.drain(drainedAmount, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE) > 0
            }

            if(anyTransfer) {
                player.setItemInHand(handIn, it.container)
            }
            return@map anyTransfer
        }.orElse(false)
        if(!fluidTransfer) {
            NetworkHooks.openGui(player as ServerPlayer, createInteration(pos), pos)
        }
        return InteractionResult.SUCCESS
    }
}