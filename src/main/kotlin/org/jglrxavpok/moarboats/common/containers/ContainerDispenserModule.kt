package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.DispenserModule

class ContainerDispenserModule(containerID: Int, playerInv: PlayerInventory, module: BoatModule, boat: IControllable): ContainerBoatModule<ContainerDispenserModule>(DispenserModule.containerType as ContainerType<ContainerDispenserModule>, containerID, playerInv, module, boat) {

    val placerInventory = boat.getInventory(module)

    init {
        val numRows = 3
        val startX = 80
        val startY = 27
        val spacing = 1
        for (j in 0 until numRows) {
            for (k in 0..4) {
                this.addSlot(Slot(placerInventory, k + j * 5, startX + k * 18, startY + j * (18+spacing) -2))
            }
        }

        addPlayerSlots(isLarge = true)
        this.addDataSlots(placerInventory.additionalData)
    }

    override fun quickMoveStack(playerIn: PlayerEntity, index: Int): ItemStack {
        return ItemStack.EMPTY // for lack of a better shift click mechanism
    }
}