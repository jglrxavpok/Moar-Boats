package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.ContainerType
import net.minecraft.util.IntReferenceHolder
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy

class EnergyContainer(containerType: ContainerType<EnergyContainer>, containerID: Int, val te: TileEntityEnergy, val player: PlayerEntity): EmptyContainer(containerID, player.inventory, containerType = containerType) {

    private var energy = -1
    private var energyHolder = object: IntReferenceHolder() {
        override fun isDirty(): Boolean {
            return energy != te.energy
        }

        override fun get(): Int {
            return energy
        }

        override fun set(arg: Int) {
            energy = arg
        }
    }

    init {
        te.addContainerListener(this)
        this.trackInt(energyHolder)
    }

    override fun onContainerClosed(playerIn: PlayerEntity?) {
        super.onContainerClosed(playerIn)
        te.removeContainerListener(this)
    }
}