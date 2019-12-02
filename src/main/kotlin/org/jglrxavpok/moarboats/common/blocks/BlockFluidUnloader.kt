package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.BlockItemUseContext
import net.minecraft.state.StateContainer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityFluidUnloader

object BlockFluidUnloader: MoarBoatsBlock() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_fluid_unloader")
        defaultState = stateContainer.baseState.with(Facing, Direction.UP)
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
        builder.add(Facing)
    }

    override fun hasTileEntity() = true
    override fun hasTileEntity(state: BlockState) = true

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return TileEntityFluidUnloader()
    }

    override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
        return this.defaultState.with(Facing, Direction.getFacingDirections(context.player)[0])
    }

    override fun onBlockActivated(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, hit: BlockRayTraceResult): Boolean {
        if(worldIn.isRemote)
            return true
        NetworkHooks.openGui(player as ServerPlayerEntity, MoarBoatsGuiHandler.FluidUnloaderGuiInteraction(pos.x, pos.y, pos.z), pos)
        return true
    }

    override fun hasComparatorInputOverride(state: BlockState): Boolean {
        return true
    }

    override fun getComparatorInputOverride(blockState: BlockState, worldIn: World, pos: BlockPos): Int {
        return (worldIn.getTileEntity(pos) as? TileEntityFluidUnloader)?.getRedstonePower() ?: 0
    }

}