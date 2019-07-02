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
import net.minecraft.world.chunk.BlockStateContainer
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergyLoader
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergyUnloader

val Facing = PropertyEnum.create("facing", EnumFacing::class.java)

object BlockEnergyUnloader: MoarBoatsBlock() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_energy_discharger")
        defaultState = stateContainer.baseState.withProperty(Facing, EnumFacing.UP)
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
        return this.defaultState.with(Facing, EnumFacing.getFacingDirections(placer))
    }

    override fun onBlockActivated(state: IBlockState, worldIn: World, pos: BlockPos, playerIn: EntityPlayer, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(worldIn.isRemote)
            return true
        playerIn.displayGui(MoarBoats, MoarBoatsGuiHandler.EnergyGui, worldIn, pos.x, pos.y, pos.z)
        return true
    }

    override fun hasComparatorInputOverride(state: IBlockState): Boolean {
        return true
    }

    override fun getComparatorInputOverride(blockState: IBlockState, worldIn: World, pos: BlockPos): Int {
        return (worldIn.getTileEntity(pos) as? TileEntityEnergyUnloader)?.getRedstonePower() ?: 0
    }

}