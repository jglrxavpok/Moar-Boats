package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy

class EnergyContainer(val te: TileEntityEnergy, val player: EntityPlayer): EmptyContainer(player.inventory) {

    private var energy = -1

    init {
        te.addContainerListener(this)
    }

    override fun onContainerClosed(playerIn: EntityPlayer?) {
        super.onContainerClosed(playerIn)
        te.removeContainerListener(this)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        for(listener in listeners) {
            if(energy != te.energy)
                listener.sendWindowProperty(this, 0, te.energy)
        }
        energy = te.energy
    }

    override fun updateProgressBar(id: Int, data: Int) {
        super.updateProgressBar(id, data)
        when(id) {
            0 -> te.energy = data
        }
    }
}