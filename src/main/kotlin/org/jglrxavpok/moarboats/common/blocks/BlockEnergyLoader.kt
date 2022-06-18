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
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergyLoader

object BlockEnergyLoader: MoarBoatsBlock() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_energy_charger")
        this.registerDefaultState(this.defaultBlockState().setValue(Facing, Direction.UP))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(Facing)
    }

    override fun hasTileEntity(state: BlockState) = true

    override fun createTileEntity(state: BlockState?, level: BlockGetter?): BlockEntity? {
        return TileEntityEnergyLoader()
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState().setValue(Facing, context.nearestLookingDirection)
    }

    override fun use(state: BlockState, worldIn: Level, pos: BlockPos, player: Player, handIn: InteractionHand, hit: BlockHitResult): InteractionResult {
        if(worldIn.isClientSide)
            return InteractionResult.SUCCESS
        NetworkHooks.openGui(player as ServerPlayer, MoarBoatsGuiHandler.EnergyChargerGuiInteraction(pos.x, pos.y, pos.z), pos)
        return InteractionResult.SUCCESS
    }

    override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    override fun getAnalogOutputSignal(blockState: BlockState, levelIn: Level, pos: BlockPos): Int {
        return (levelIn.getBlockEntity(pos) as? TileEntityEnergyLoader)?.getRedstonePower() ?: 0
    }

}