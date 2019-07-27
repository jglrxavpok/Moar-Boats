package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.Container
import net.minecraft.inventory.InventoryHelper
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
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

    override fun hasTileEntity() = true

    override fun hasTileEntity(state: IBlockState) = true

    override fun createTileEntity(state: IBlockState?, world: IBlockReader?): TileEntity? {
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
        NetworkHooks.openGui(playerIn as EntityPlayerMP, MoarBoatsGuiHandler.MappingTableGuiInteraction(pos.x, pos.y, pos.z))
        return true
    }
}