package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.MenuTypes
import org.jglrxavpok.moarboats.common.modules.DispenserModule

class ContainerDispenserModule(containerID: Int, playerInv: Inventory, module: BoatModule, boat: IControllable): ContainerBoatModule<ContainerDispenserModule>(
    ContainerTypes.DispenserModuleMenu.get()  as MenuType<ContainerDispenserModule>, containerID, playerInv, boat) {

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

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
        return ItemStack.EMPTY // for lack of a better shift click mechanism
    }
}