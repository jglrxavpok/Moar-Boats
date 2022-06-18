package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.ShulkerBoxMenu
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity

class UtilityChestContainer(windowID: Int, playerInv: Inventory, tileEntity: ChestBlockEntity): ChestMenu(ContainerTypes.ChestBoat.get(), windowID, playerInv, tileEntity, 3) {

    override fun stillValid(playerIn: Player): Boolean {
        return true
    }
}

class UtilityShulkerContainer(windowID: Int, playerInv: Inventory, tileEntity: ShulkerBoxBlockEntity): ShulkerBoxMenu(windowID, playerInv, tileEntity) {

    override fun stillValid(playerIn: Player): Boolean {
        return true
    }

    override fun getType(): MenuType<*> {
        return ContainerTypes.ShulkerBoat.get()
    }
}
