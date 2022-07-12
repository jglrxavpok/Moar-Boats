package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.entity.player.Player
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergyLoader
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergyUnloader

class BlockEnergyLoader: MoarBoatsBlockEntity() {

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(Facing, Direction.UP))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(Facing)
    }

    override fun newBlockEntity(p_153215_: BlockPos, p_153216_: BlockState): BlockEntity? {
        return TileEntityEnergyLoader(p_153215_, p_153216_)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState().setValue(Facing, context.nearestLookingDirection)
    }

    override fun use(state: BlockState, worldIn: Level, pos: BlockPos, player: Player, handIn: InteractionHand, hit: BlockHitResult): InteractionResult {
        if(worldIn.isClientSide)
            return InteractionResult.SUCCESS
        NetworkHooks.openScreen(player as ServerPlayer, MoarBoatsGuiHandler.EnergyChargerGuiInteraction(pos.x, pos.y, pos.z), pos)
        return InteractionResult.SUCCESS
    }

    override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    override fun getAnalogOutputSignal(blockState: BlockState, levelIn: Level, pos: BlockPos): Int {
        return (levelIn.getBlockEntity(pos) as? TileEntityEnergyLoader)?.getRedstonePower() ?: 0
    }

}