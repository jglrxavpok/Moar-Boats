package org.jglrxavpok.moarboats.api

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.inventory.container.INamedContainerProvider
import org.jglrxavpok.moarboats.MoarBoats

class BoatModuleInteractionObject(val module: BoatModule, val boat: IControllable) : INamedContainerProvider {

    override fun hasCustomName() = false

    override fun getCustomName(): ITextComponent? = null

    override fun getName() = TranslationTextComponent("inventory.${module.id.path}.name")

    override fun getGuiID(): String {
        return "${MoarBoats.ModID}:gui/modules/${boat.entityID}/${boat.modules.indexOf(module)}"
    }

    override fun createMenu(playerInventory: PlayerInventory, playerIn: PlayerEntity): Container? {
        return module.createContainer(playerIn, boat)
    }
}
