package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.inventory.Container
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

abstract class TileEntityListenable: TileEntity() {

    private val listeners = mutableListOf<Container>()
    private val listenersToAdd = mutableListOf<Container>()
    private val listenersToRemove = mutableListOf<Container>()

    fun removeContainerListener(container: Container) {
        listenersToRemove += container
    }

    fun addContainerListener(container: Container) {
        listenersToAdd += container
    }

    fun updateListeners() {
        listeners.addAll(listenersToAdd)
        listeners.removeAll(listenersToRemove)

        listenersToAdd.clear()
        listenersToRemove.clear()
        for(listener in listeners) {
            listener.detectAndSendChanges()
        }
    }

    /**
     * Create a 3x3 BB around the given BlockPos
     */
    fun create3x3AxisAlignedBB(pos: BlockPos): AxisAlignedBB {
        val minX = pos.x-1.0
        val minY = pos.y-1.0
        val minZ = pos.z-1.0
        val maxX = pos.x+2.0
        val maxY = pos.y+2.0
        val maxZ = pos.z+2.0
        return AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
    }
}