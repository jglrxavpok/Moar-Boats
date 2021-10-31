package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.BlockItemUseContext
import net.minecraft.state.StateContainer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ActionResultType
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
import org.jglrxavpok.moarboats.common.tileentity.TileEntityFluidLoader

object BlockFluidLoader: MoarBoatsBlock() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_fluid_loader")
        this.registerDefaultState(this.defaultBlockState().setValue(Facing, Direction.UP))
    }

    override fun createBlockStateDefinition(builder: StateContainer.Builder<Block, BlockState>) {
        builder.add(Facing)
    }

    override fun hasTileEntity(state: BlockState) = true

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return TileEntityFluidLoader()
    }

    override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
        return this.defaultBlockState().setValue(Facing, Direction.getFacingDirections(context.player)[0])
    }

    override fun onUse(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, hit: BlockRayTraceResult): ActionResultType {
        if(worldIn.isClientSide)
            return ActionResultType.SUCCESS
        NetworkHooks.openGui(player as ServerPlayerEntity, MoarBoatsGuiHandler.FluidLoaderGuiInteraction(pos.x, pos.y, pos.z), pos)
        return ActionResultType.SUCCESS
    }

    override fun hasComparatorInputOverride(state: BlockState): Boolean {
        return true
    }

    override fun getComparatorInputOverride(blockState: BlockState, worldIn: World, pos: BlockPos): Int {
        return (worldIn.getBlockEntity(pos) as? TileEntityFluidLoader)?.getRedstonePower() ?: 0
    }

}