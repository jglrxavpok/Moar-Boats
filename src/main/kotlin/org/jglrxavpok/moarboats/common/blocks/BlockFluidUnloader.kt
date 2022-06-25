package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.ITickableTileEntity
import org.jglrxavpok.moarboats.common.tileentity.TileEntityFluidLoader
import org.jglrxavpok.moarboats.common.tileentity.TileEntityFluidUnloader

class BlockFluidUnloader: FluidStoringBlock<TileEntityFluidUnloader>() {

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(Facing, Direction.UP))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(Facing)
    }

    override fun newBlockEntity(p_153215_: BlockPos, p_153216_: BlockState): BlockEntity? {
        return TileEntityFluidUnloader(p_153215_, p_153216_)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState().setValue(Facing, Direction.orderedByNearest(context.player)[0])
    }

    override fun createInteration(blockPos: BlockPos): MoarBoatsGuiHandler.TileEntityInteraction<TileEntityFluidUnloader> {
        return MoarBoatsGuiHandler.FluidUnloaderGuiInteraction(blockPos.x, blockPos.y, blockPos.z)
    }

}