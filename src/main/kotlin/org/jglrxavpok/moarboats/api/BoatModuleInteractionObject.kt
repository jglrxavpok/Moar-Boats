package org.jglrxavpok.moarboats.api

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import org.jglrxavpok.moarboats.MoarBoats

class BoatModuleInteractionObject(val module: BoatModule, val boat: IControllable): INamedContainerProvider {
    override fun createMenu(counter: Int, playerInventory: PlayerInventory, playerIn: PlayerEntity): Container? {
        return module.createContainer(counter, playerIn, boat)
    }

    //    override fun getDisplayName() = TranslationTextComponent("inventory.${module.id.path}.name")

    override fun getDisplayName(): ITextComponent {
        return StringTextComponent(module.id.toString())
    }

}
