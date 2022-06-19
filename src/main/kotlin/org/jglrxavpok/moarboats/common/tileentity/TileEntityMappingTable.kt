package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.ContainerHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.extensions.IForgeBlockEntity
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.wrapper.InvWrapper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.BlockEntities

class TileEntityMappingTable(blockPos: BlockPos, blockState: BlockState): BlockEntity(BlockEntities.MappingTable.get(), blockPos, blockState),
    IForgeBlockEntity {

    val inventory = SimpleContainer(1)
    val invWrapper = InvWrapper(inventory)

    override fun saveAdditional(compound: CompoundTag) {
        super.saveAdditional(compound)
        val invList = NonNullList.withSize(inventory.containerSize, ItemStack.EMPTY)
        for(i in 0 until inventory.containerSize) {
            invList[i] = inventory.getItem(i) ?: ItemStack.EMPTY
        }
        ContainerHelper.saveAllItems(compound, invList)
    }

    override fun load(compound: CompoundTag) {
        super.load(compound)
        val invList = NonNullList.withSize(inventory.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(compound, invList)
        inventory.clearContent()
        for(i in 0 until inventory.containerSize) {
            inventory.setItem(i, invList.get(i))
        }
    }

    override fun <T> getCapability(capability: net.minecraftforge.common.capabilities.Capability<T>, facing: Direction?): LazyOptional<T> {
        if (capability === net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of { invWrapper }.cast()
        }
        return super<BlockEntity>.getCapability(capability, facing)
    }
}