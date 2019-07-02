package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

object BlockMappingTable: MoarBoatsBlock({ sound(SoundType.STONE).hardnessAndResistance(2.5f, 20f)}) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "mapping_table")
    }

    override fun hasTileEntity() = true

    override fun hasTileEntity(state: IBlockState) = true

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return TileEntityMappingTable()
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    //IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving)
    override fun onReplaced(state: IBlockState, worldIn: World, pos: BlockPos, newState: IBlockState, isMoving: Boolean) {
        val tileentity = worldIn.getTileEntity(pos)

        if (tileentity is TileEntityMappingTable) {
            InventoryHelper.dropInventoryItems(worldIn, pos, tileentity.inventory)
            worldIn.updateComparatorOutputLevel(pos, this)
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving)
    }

    override fun hasComparatorInputOverride(state: IBlockState): Boolean {
        return true
    }

    override fun getComparatorInputOverride(blockState: IBlockState, worldIn: World, pos: BlockPos): Int {
        return (worldIn.getTileEntity(pos) as? TileEntityMappingTable)?.let {
            Container.calcRedstoneFromInventory(it.inventory)
        } ?: 0
    }

    override fun onBlockActivated(state: IBlockState, worldIn: World, pos: BlockPos, playerIn: EntityPlayer, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(worldIn.isRemote) {
            return true
        }
        playerIn.displayGui(MoarBoats, MoarBoatsGuiHandler.MappingTableGui, worldIn, pos.x, pos.y, pos.z)
        return true
    }
}