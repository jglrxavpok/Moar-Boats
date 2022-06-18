package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.core.BlockPos
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraftforge.common.extensions.IForgeBlockEntity

abstract class TileEntityListenable(tileEntityType: BlockEntityType<out TileEntityListenable>, blockPos: BlockPos, blockState: BlockState): BlockEntity(tileEntityType, blockPos, blockState),
    IForgeBlockEntity {

    private val listeners = mutableListOf<AbstractContainerMenu>()
    private val listenersToAdd = mutableListOf<AbstractContainerMenu>()
    private val listenersToRemove = mutableListOf<AbstractContainerMenu>()

    abstract fun getRedstonePower(): Int

    fun removeContainerListener(container: AbstractContainerMenu) {
        listenersToRemove += container
    }

    fun addContainerListener(container: AbstractContainerMenu) {
        listenersToAdd += container
    }

    fun updateListeners() {
        listeners.addAll(listenersToAdd)
        listeners.removeAll(listenersToRemove)

        listenersToAdd.clear()
        listenersToRemove.clear()
        for(listener in listeners) {
            listener.broadcastChanges()
        }
    }

    /**
     * Create a 3x3 BB around the given BlockPos
     */
    fun create3x3AxisAlignedBB(pos: BlockPos): AABB {
        val minX = pos.x-1.0
        val minY = pos.y-1.0
        val minZ = pos.z-1.0
        val maxX = pos.x+2.0
        val maxY = pos.y+2.0
        val maxZ = pos.z+2.0
        return AABB(minX, minY, minZ, maxX, maxY, maxZ)
    }
}