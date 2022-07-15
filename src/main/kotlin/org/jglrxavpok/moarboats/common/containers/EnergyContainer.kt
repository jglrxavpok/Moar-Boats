package org.jglrxavpok.moarboats.common.containers

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.DataSlot
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy

class EnergyContainer(isLoading: Boolean, containerID: Int, val te: TileEntityEnergy, val player: Player): EmptyContainer(containerID, player.inventory,
    containerType = if(isLoading) {
        ContainerTypes.EnergyCharger.get()
    } else {
        ContainerTypes.EnergyDischarger.get()
    }) {

    var energy = -1
        private set
    private var energyHolder = object: DataSlot() {
/*        override fun checkAndClearUpdateFlag(): Boolean {
            return energy != te.energy
        }*/

        override fun get(): Int {
            return energy
        }

        override fun set(arg: Int) {
            energy = arg
        }
    }

    init {
        te.addContainerListener(this)
        this.addDataSlot(energyHolder)
    }

    override fun removed(playerIn: Player?) {
        super.removed(playerIn)
        te.removeContainerListener(this)
    }

    override fun broadcastChanges() {
        super.broadcastChanges()
        if(player !is ServerPlayer)
            return
        val teEnergy = te.energy
        energyHolder.set(teEnergy)
        setData(0, teEnergy)
    }
}