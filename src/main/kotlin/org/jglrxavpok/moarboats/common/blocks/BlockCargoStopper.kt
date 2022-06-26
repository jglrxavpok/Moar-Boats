package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DiodeBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

class BlockCargoStopper: DiodeBlock(Properties.of(Material.DECORATION).noOcclusion().randomTicks().strength(0f).sound(SoundType.WOOD)) {
    init {
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(POWERED, false))
    }

    override fun isRandomlyTicking(state: BlockState) = true

    override fun getSignal(p_52520_: BlockState, p_52521_: BlockGetter, p_52522_: BlockPos, p_52523_: Direction): Int {
        return if (!p_52520_.getValue(POWERED)) {
            0
        } else {
            if (p_52520_.getValue(FACING).opposite != p_52523_) getOutputSignal(p_52521_, p_52522_, p_52520_) else 0
        }
    }

    override fun canConnectRedstone(state: BlockState?, world: BlockGetter?, pos: BlockPos?, side: Direction?): Boolean {
        return side != null && side != Direction.DOWN && side != Direction.UP
    }

    override fun getCollisionShape(state: BlockState, worldIn: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.empty()
    }

    override fun canSurvive(state: BlockState, worldIn: LevelReader, pos: BlockPos): Boolean {
        return worldIn.getFluidState(pos.below()).`is`(FluidTags.WATER)
    }

    override fun getDelay(state: BlockState?): Int {
        return 0
    }

    override fun getDirectSignal(state: BlockState, blockAccess: BlockGetter, pos: BlockPos, side: Direction): Int {
        if(blockAccess is Level) {
            val world = blockAccess
            val aabb = AABB(pos.relative(state.getValue(FACING)))
            val entities = world.getEntitiesOfClass(BasicBoatEntity::class.java, aabb) { e -> e != null && e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent }
            val first = entities.firstOrNull()
            return first?.let {
                it.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).map { capa -> calcRedstoneFromInventory(capa) }.orElse(0)
            } ?: 0
        }
        return 0
    }

    override fun tick(state: BlockState, worldIn: ServerLevel, pos: BlockPos, rand: RandomSource) {
        val produceSignal = shouldTurnOn(worldIn, pos, state)
        when {
            produceSignal && !state.getValue(POWERED) -> worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, true).setValue(FACING, state.getValue(FACING)))
            !produceSignal && state.getValue(POWERED) -> worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, false).setValue(FACING, state.getValue(FACING)))
        }
        worldIn.scheduleTick(pos, this, 2)
        checkTickOnNeighbor(worldIn, pos, state)
    }

    private fun calcRedstoneFromInventory(inv: IItemHandler?): Int {
        if (inv == null) {
            return 0
        } else {
            var i = 0
            var f = 0.0f

            for (slotIndex in 0 until inv.slots) {
                val itemstack = inv.getStackInSlot(slotIndex)

                if (!itemstack.isEmpty) {
                    f += itemstack.count.toFloat() / Math.min(inv.getSlotLimit(slotIndex), itemstack.maxStackSize).toFloat()
                    ++i
                }
            }

            f /= inv.slots.toFloat()
            return Mth.floor(f * 14.0f) + if (i > 0) 1 else 0
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, POWERED)
    }

    override fun onRemove(state: BlockState, worldIn: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, worldIn, pos, newState, isMoving)
        this.checkTickOnNeighbor(worldIn, pos, state)
    }

    /**
     * Called by BlockItems after a block is set in the world, to allow post-place logic
     */
    override fun setPlacedBy(worldIn: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack)
        //worldIn.pendingBlockTicks.scheduleTick(pos, this, 2)
        this.checkTickOnNeighbor(worldIn, pos, state)
    }

    override fun shouldTurnOn(worldIn: Level, pos: BlockPos, state: BlockState): Boolean {
        return getDirectSignal(state, worldIn, pos, state.getValue(FACING)) > 0
    }

    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        return mutableListOf(ItemStack(MBItems.CargoStopperItem.get(), 1))
    }

    override fun getOutputSignal(p_52541_: BlockGetter, p_52542_: BlockPos, p_52543_: BlockState): Int {
        return super.getOutputSignal(p_52541_, p_52542_, p_52543_)
    }

    override fun getCloneItemStack(worldIn: BlockGetter, pos: BlockPos, state: BlockState) = ItemStack(MBItems.CargoStopperItem.get(), 1)

    override fun getWeakChanges(state: BlockState, world: LevelReader?, pos: BlockPos?): Boolean {
        return true
    }

}