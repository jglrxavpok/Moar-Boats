package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler

object BlockMappingTable: Block(Material.WOOD) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "mapping_table")
        unlocalizedName = "mapping_table"
        setCreativeTab(MoarBoats.CreativeTab)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(worldIn.isRemote) {
            return true
        }
        playerIn.openGui(MoarBoats, MoarBoatsGuiHandler.MappingTableGui, worldIn, pos.x, pos.y, pos.z)
        return true
    }
}