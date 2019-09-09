package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.SoundType
import net.minecraft.block.state.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.InventoryHelper
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

object BlockMappingTable: MoarBoatsBlock({ sound(SoundType.STONE).strength(2.5f, 20f)}) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "mapping_table")
    }

    override fun isEntityBlock() = true

    override fun hasTileEntity(state: BlockState) = true

    override fun createTileEntity(state: BlockState?, level: IBlockReader?): TileEntity? {
        return TileEntityMappingTable()
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    //BlockState state, level levelIn, BlockPos pos, BlockState newState, boolean isMoving)
    override fun onReplaced(state: BlockState, levelIn: World, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        val tileentity = levelIn.getBlockEntity(pos)

        if (tileentity is TileEntityMappingTable) {
            InventoryHelper.dropContents(levelIn, pos, tileentity.inventory)
            levelIn.updateComparatorOutputLevel(pos, this)
        }

        super.onReplaced(state, levelIn, pos, newState, isMoving)
    }

    override fun hasComparatorInputOverride(state: BlockState): Boolean {
        return true
    }

    override fun getComparatorInputOverride(blockState: BlockState, levelIn: World, pos: BlockPos): Int {
        return (levelIn.getBlockEntity(pos) as? TileEntityMappingTable)?.let {
            Container.getRedstoneSignalFromContainer(it.inventory)
        } ?: 0
    }

    override fun onBlockActivated(state: BlockState, levelIn: World, pos: BlockPos, playerIn: PlayerEntity, hand: Hand?, facing: Direction?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(levelIn.isClientSide) {
            return true
        }
        NetworkHooks.openGui(playerIn as ServerPlayerEntity, MoarBoatsGuiHandler.MappingTableGuiInteraction(pos.x, pos.y, pos.z))
        return true
    }
}