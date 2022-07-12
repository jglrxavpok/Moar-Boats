package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class BlockMappingTable: MoarBoatsBlockEntity({ sound(SoundType.STONE).strength(2.5f, 20f)}) {

    override fun newBlockEntity(p_153215_: BlockPos, p_153216_: BlockState): BlockEntity? {
        return TileEntityMappingTable(p_153215_, p_153216_)
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    override fun onRemove(state: BlockState, levelIn: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        val tileentity = levelIn.getBlockEntity(pos)

        if (tileentity is TileEntityMappingTable) {
            Containers.dropContents(levelIn, pos, tileentity.inventory)
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
        NetworkHooks.openScreen(playerIn as ServerPlayer, MoarBoatsGuiHandler.MappingTableGuiInteraction(pos.x, pos.y, pos.z), pos)
        return InteractionResult.SUCCESS
    }
}