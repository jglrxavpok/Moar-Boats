package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.inventory.Container
import net.minecraft.tileentity.TileEntity

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
}