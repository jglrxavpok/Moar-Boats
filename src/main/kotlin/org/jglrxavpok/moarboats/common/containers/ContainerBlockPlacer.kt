package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

class ContainerBlockPlacer(inventoryPlayer: InventoryPlayer, module: BoatModule, boat: IControllable): ContainerBase(inventoryPlayer) {

    val placerInventory = boat.getInventory(module)

    init {
        val numRows = 3
        val startX = 80
        val startY = 27
        val spacing = 1
        for (j in 0 until numRows) {
            for (k in 0..4) {
                this.addSlotToContainer(Slot(placerInventory, k + j * 9, startX + k * 18, startY + j * (18+spacing) -2))
            }
        }

        addPlayerSlots(isLarge = true)
    }
}