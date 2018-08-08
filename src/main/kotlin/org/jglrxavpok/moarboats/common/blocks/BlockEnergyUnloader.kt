package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockDirectional
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergyLoader
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergyUnloader

val Facing = PropertyEnum.create("facing", EnumFacing::class.java)

object BlockEnergyUnloader: Block(Material.IRON) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_energy_unloader")
        unlocalizedName = "boat_energy_unloader"
        setCreativeTab(MoarBoats.CreativeTab)
        defaultState = blockState.baseState.withProperty(Facing, EnumFacing.UP)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, Facing)
    }

    override fun hasTileEntity() = true
    override fun hasTileEntity(state: IBlockState) = true

    override fun createTileEntity(world: World?, state: IBlockState?): TileEntity? {
        return TileEntityEnergyUnloader()
    }

    override fun getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        return this.defaultState.withProperty(BlockDirectional.FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer))
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(Facing, EnumFacing.values()[meta % EnumFacing.values().size])
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(Facing).ordinal
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(worldIn.isRemote)
            return true
        playerIn.openGui(MoarBoats, MoarBoatsGuiHandler.EnergyGui, worldIn, pos.x, pos.y, pos.z)
        return true
    }

}