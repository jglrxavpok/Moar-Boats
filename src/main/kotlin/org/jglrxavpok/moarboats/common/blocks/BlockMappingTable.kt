package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.inventory.InventoryHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.math.BlockHitResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

object BlockMappingTable: MoarBoatsBlock({ sound(SoundType.STONE).strength(2.5f, 20f)}) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "mapping_table")
    }

    override fun hasTileEntity(state: BlockState) = true

    override fun createTileEntity(state: BlockState?, level: BlockGetter?): BlockEntity? {
        return TileEntityMappingTable()
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    override fun onRemove(state: BlockState, levelIn: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        val tileentity = levelIn.getBlockEntity(pos)

        if (tileentity is TileEntityMappingTable) {
            InventoryHelper.dropContents(levelIn, pos, tileentity.inventory)
            levelIn.updateNeighbourForOutputSignal(pos, this)
        }

        super.onRemove(state, levelIn, pos, newState, isMoving)
    }

    override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    override fun getAnalogOutputSignal(blockState: BlockState, levelIn: Level, pos: BlockPos): Int {
        return (levelIn.getBlockEntity(pos) as? TileEntityMappingTable)?.let {
            AbstractContainerMenu.getRedstoneSignalFromContainer(it.inventory)
        } ?: 0
    }

    override fun use(state: BlockState, levelIn: Level, pos: BlockPos, playerIn: Player, hand: InteractionHand?, hit: BlockHitResult): InteractionResult {
        if(levelIn.isClientSide) {
            return InteractionResult.SUCCESS
        }
        NetworkHooks.openGui(playerIn as ServerPlayer, MoarBoatsGuiHandler.MappingTableGuiInteraction(pos.x, pos.y, pos.z), pos)
        return InteractionResult.SUCCESS
    }
}