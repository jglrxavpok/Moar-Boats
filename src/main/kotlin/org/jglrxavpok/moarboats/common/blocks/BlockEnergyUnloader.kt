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
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergyUnloader

val Facing: DirectionProperty = DirectionProperty.create("facing") {true}

object BlockEnergyUnloader: MoarBoatsBlock() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_energy_discharger")
        this.registerDefaultState(this.defaultBlockState().setValue(Facing, Direction.UP))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(Facing)
    }

    override fun hasTileEntity(state: BlockState) = true

    override fun createTileEntity(state: BlockState?, world: BlockGetter?): BlockEntity? {
        return TileEntityEnergyUnloader()
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState().setValue(Facing, context.nearestLookingDirection)
    }

    override fun use(state: BlockState, worldIn: Level, pos: BlockPos, playerIn: Player, hand: InteractionHand?, hit: BlockHitResult): InteractionResult {
        if(worldIn.isClientSide)
            return InteractionResult.SUCCESS
        NetworkHooks.openGui(playerIn as ServerPlayer, MoarBoatsGuiHandler.EnergyDischargerGuiInteraction(pos.x, pos.y, pos.z), pos)
        return InteractionResult.SUCCESS
    }

    override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    override fun getAnalogOutputSignal(blockState: BlockState, worldIn: Level, pos: BlockPos): Int {
        return (worldIn.getBlockEntity(pos) as? TileEntityEnergyUnloader)?.getRedstonePower() ?: 0
    }

}