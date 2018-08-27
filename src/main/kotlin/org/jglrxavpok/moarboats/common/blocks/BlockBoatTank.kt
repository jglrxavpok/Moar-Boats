package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

object BlockBoatTank: Block(MoarBoats.MachineMaterial) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_tank")
        unlocalizedName = "boat_tank"
        setCreativeTab(MoarBoats.CreativeTab)
        setHardness(0.5f)
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean {
        return false
    }

    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun isFullBlock(state: IBlockState?): Boolean {
        return false
    }

    override fun addInformation(stack: ItemStack?, player: World?, tooltip: MutableList<String>, advanced: ITooltipFlag?) {
        super.addInformation(stack, player, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats.unformattedText)
    }

}