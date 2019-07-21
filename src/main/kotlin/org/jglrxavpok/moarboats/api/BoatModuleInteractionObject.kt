package org.jglrxavpok.moarboats.api

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.IInteractionObject

class BoatModuleInteractionObject(val module: BoatModule, val boat: IControllable) : IInteractionObject {


    override fun hasCustomName() = false

    override fun getCustomName(): ITextComponent? = null

    override fun getName() = TextComponentTranslation("inventory.${module.id.path}.name")

    override fun getGuiID(): String {
        return module.id.path
    }

    override fun createContainer(playerInventory: InventoryPlayer, playerIn: EntityPlayer): Container? {
        return module.createContainer(playerIn, boat)
    }
}
