package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.BlockRedstoneDiode
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.state.StateContainer
import net.minecraft.tags.FluidTags
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.util.math.shapes.VoxelShapes
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorldReader
import net.minecraft.world.IWorldReaderBase
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.items.CargoStopperItem
import java.util.*

object BlockCargoStopper: BlockRedstoneDiode(Block.Properties.create(Material.CIRCUITS).tickRandomly().hardnessAndResistance(0f).sound(SoundType.WOOD)) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "cargo_stopper")
        this.defaultState = this.stateContainer.baseState.with(BlockHorizontal.HORIZONTAL_FACING, EnumFacing.NORTH).with(POWERED, false)
    }

    override fun ticksRandomly(state: IBlockState) = true

    override fun canConnectRedstone(state: IBlockState?, world: IBlockReader?, pos: BlockPos?, side: EnumFacing?): Boolean {
        return side != null && side != EnumFacing.DOWN && side != EnumFacing.UP
    }

    override fun getCollisionShape(state: IBlockState, worldIn: IBlockReader, pos: BlockPos): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun isValidPosition(state: IBlockState, worldIn: IWorldReaderBase, pos: BlockPos): Boolean {
        return worldIn.getFluidState(pos.down()).isTagged(FluidTags.WATER)
    }

    override fun getDelay(state: IBlockState?): Int {
        return 0
    }

    override fun getWeakPower(state: IBlockState, blockAccess: IBlockReader, pos: BlockPos, side: EnumFacing): Int {
        if(blockAccess is World) {
            val world = blockAccess
            val aabb = AxisAlignedBB(pos.offset(state.get(BlockHorizontal.HORIZONTAL_FACING)))
            val entities = world.getEntitiesWithinAABB(BasicBoatEntity::class.java, aabb) { e -> e != null && e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent }
            val first = entities.firstOrNull()
            return first?.let {
                it.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).map { capa -> calcRedstoneFromInventory(capa) }.orElse(0)
            } ?: 0
        }
        return 0
    }

    override fun tick(state: IBlockState, worldIn: World, pos: BlockPos, random: Random) {
        val produceSignal = shouldBePowered(worldIn, pos, state)
        when {
            produceSignal && !state[POWERED] -> worldIn.setBlockState(pos, state.with(POWERED, true).with(BlockHorizontal.HORIZONTAL_FACING, state.get(BlockHorizontal.HORIZONTAL_FACING)))
            !produceSignal && state[POWERED] -> worldIn.setBlockState(pos, state.with(POWERED, false).with(BlockHorizontal.HORIZONTAL_FACING, state.get(BlockHorizontal.HORIZONTAL_FACING)))
        }
        worldIn.pendingBlockTicks.scheduleTick(pos, this, 2)
        notifyNeighbors(worldIn, pos, state)
    }

    private fun calcRedstoneFromInventory(inv: IItemHandler?): Int {
        if (inv == null) {
            return 0
        } else {
            var i = 0
            var f = 0.0f

            for (slotIndex in 0 until inv.slots) {
                val itemstack = inv.getItem(slotIndex)

                if (!itemstack.isEmpty) {
                    f += itemstack.count.toFloat() / Math.min(inv.getSlotLimit(slotIndex), itemstack.maxStackSize).toFloat()
                    ++i
                }
            }

            f /= inv.slots.toFloat()
            return MathHelper.floor(f * 14.0f) + if (i > 0) 1 else 0
        }
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block, IBlockState>) {
        builder.add(BlockHorizontal.HORIZONTAL_FACING, POWERED)
    }

    override fun onReplaced(state: IBlockState, worldIn: World, pos: BlockPos, newState: IBlockState, isMoving: Boolean) {
        super.onReplaced(state, worldIn, pos, newState, isMoving)
        this.notifyNeighbors(worldIn, pos, state)
    }

    /**
     * Called by BlockItems after a block is set in the world, to allow post-place logic
     */
    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase?, stack: ItemStack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        //worldIn.pendingBlockTicks.scheduleTick(pos, this, 2)
        this.notifyNeighbors(worldIn, pos, state)
    }

    override fun shouldBePowered(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        return getWeakPower(state, worldIn, pos, state.get(BlockHorizontal.HORIZONTAL_FACING)) > 0
    }

    override fun getItemDropped(state: IBlockState, worldIn: World, pos: BlockPos, fortune: Int) = CargoStopperItem

    override fun getItem(worldIn: IBlockReader, pos: BlockPos, state: IBlockState) = ItemStack(CargoStopperItem, 1)

    override fun getWeakChanges(state: IBlockState?, world: IWorldReader?, pos: BlockPos?): Boolean {
        return true
    }

}