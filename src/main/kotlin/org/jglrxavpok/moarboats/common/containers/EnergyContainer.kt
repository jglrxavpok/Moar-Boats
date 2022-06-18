package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.DataSlot
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy

class EnergyContainer(containerType: MenuType<EnergyContainer>, containerID: Int, val te: TileEntityEnergy, val player: Player): EmptyContainer(containerID, player.inventory, containerType = containerType) {

    private var energy = -1
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
}