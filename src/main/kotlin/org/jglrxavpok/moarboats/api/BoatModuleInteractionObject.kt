package org.jglrxavpok.moarboats.api

import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

class BoatModuleInteractionObject(val module: BoatModule, val boat: IControllable): MenuProvider {
    override fun createMenu(counter: Int, playerInventory: Inventory, playerIn: Player): AbstractContainerMenu? {
        return module.createContainer(counter, playerIn, boat)
    }

    //    override fun getDisplayName() = Component.translatable("inventory.${module.id.path}.name")

    override fun getDisplayName(): Component {
        return Component.literal(module.id.toString())
    }

}
