package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ChestContainer
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.ShulkerBoxContainer
import net.minecraft.tileentity.ChestTileEntity
import net.minecraft.tileentity.ShulkerBoxTileEntity

class UtilityChestContainer(windowID: Int, playerInv: PlayerInventory, tileEntity: ChestTileEntity): ChestContainer(ContainerTypes.ChestBoat, windowID, playerInv, tileEntity, 3) {

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}

class UtilityShulkerContainer(windowID: Int, playerInv: PlayerInventory, tileEntity: ShulkerBoxTileEntity): ShulkerBoxContainer(windowID, playerInv, tileEntity) {

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }

    override fun getType(): ContainerType<*> {
        return ContainerTypes.ShulkerBoat
    }
}
