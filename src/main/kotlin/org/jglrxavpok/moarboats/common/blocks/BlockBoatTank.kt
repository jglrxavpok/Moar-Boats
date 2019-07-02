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
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.IBlockAccess
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats

object BlockBoatTank: MoarBoatsBlock() {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_tank")
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

    override fun addInformation(stack: ItemStack, world: IBlockReader?, tooltip: MutableList<ITextComponent>, advanced: ITooltipFlag) {
        super.addInformation(stack, world, tooltip, advanced)
        tooltip.add(OnlyUsableOnBoats)
    }

}