package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerEntityMP
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.InventoryHelper
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

object BlockMappingTable: MoarBoatsBlock({ sound(SoundType.STONE).hardnessAndResistance(2.5f, 20f)}) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "mapping_table")
    }

    override fun isEntityBlock() = true

    override fun hasTileEntity(state: IBlockState) = true

    override fun createTileEntity(state: IBlockState?, level: IBlockReader?): TileEntity? {
        return TileEntityMappingTable()
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    //IBlockState state, level levelIn, BlockPos pos, IBlockState newState, boolean isMoving)
    override fun onReplaced(state: IBlockState, levelIn: World, pos: BlockPos, newState: IBlockState, isMoving: Boolean) {
        val tileentity = levelIn.getBlockEntity(pos)

        if (tileentity is TileEntityMappingTable) {
            InventoryHelper.dropInventoryItems(levelIn, pos, tileentity.inventory)
            levelIn.updateComparatorOutputLevel(pos, this)
        }

        super.onReplaced(state, levelIn, pos, newState, isMoving)
    }

    override fun hasComparatorInputOverride(state: IBlockState): Boolean {
        return true
    }

    override fun getComparatorInputOverride(blockState: IBlockState, levelIn: World, pos: BlockPos): Int {
        return (levelIn.getBlockEntity(pos) as? TileEntityMappingTable)?.let {
            Container.calcRedstoneFromInventory(it.inventory)
        } ?: 0
    }

    override fun onBlockActivated(state: IBlockState, levelIn: World, pos: BlockPos, playerIn: PlayerEntity, hand: Hand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(levelIn.isClientSide) {
            return true
        }
        NetworkHooks.openGui(playerIn as PlayerEntityMP, MoarBoatsGuiHandler.MappingTableGuiInteraction(pos.x, pos.y, pos.z))
        return true
    }
}