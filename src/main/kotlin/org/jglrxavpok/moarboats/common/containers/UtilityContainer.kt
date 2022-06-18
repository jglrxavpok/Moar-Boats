package org.jglrxavpok.moarboats.common.containers

import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraftforge.fml.util.ObfuscationReflectionHelper

class UtilityContainer<T: AbstractContainerMenu>(type: MenuType<T>, id: Int, val baseContainer: AbstractContainerMenu): AbstractContainerMenu(type, id) {

    companion object {
        // TODO: Access transformer
        val resetDragMethod = ObfuscationReflectionHelper.findMethod(AbstractContainerMenu::class.java, "func_94533_d")
        val clearContainerMethod = ObfuscationReflectionHelper.findMethod(AbstractContainerMenu::class.java, "func_193327_a", Player::class.java, Level::class.java, AbstractContainerMenu::class.java)
        val moveItemStackToMethod = ObfuscationReflectionHelper.findMethod(AbstractContainerMenu::class.java, "func_75135_a", ItemStack::class.java, Integer.TYPE, Integer.TYPE, java.lang.Boolean.TYPE)
    }

    override fun stillValid(playerIn: Player): Boolean {
        return true
    }

    override fun canDragTo(slotIn: Slot): Boolean {
        return baseContainer.canDragTo(slotIn)
    }

    override fun canTakeItemForPickAll(stack: ItemStack, slotIn: Slot): Boolean {
        return baseContainer.canTakeItemForPickAll(stack, slotIn)
    }

    override fun transferState(menu: AbstractContainerMenu) {
        baseContainer.transferState(menu)
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
        return baseContainer.quickMoveStack(playerIn, index)
    }

    override fun addSlotListener(listener: ContainerListener) {
        baseContainer.addSlotListener(listener)
    }

    override fun clickMenuButton(playerIn: Player, id: Int): Boolean {
        return baseContainer.clickMenuButton(playerIn, id)
    }

    override fun removeSlotListener(listener: ContainerListener) {
        baseContainer.removeSlotListener(listener)
    }

    override fun setData(id: Int, data: Int) {
        baseContainer.setData(id, data)
    }

    override fun removed(playerIn: Player) {
        baseContainer.removed(playerIn)
    }

    override fun setItem(slotID: Int, stateID: Int, stack: ItemStack) {
        baseContainer.setItem(slotID, stateID, stack)
    }

    override fun clicked(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: Player) {
        baseContainer.clicked(slotId, dragType, clickTypeIn, player)
    }

    override fun getSlot(slotId: Int): Slot {
        return baseContainer.getSlot(slotId)
    }

    override fun getItems(): NonNullList<ItemStack> {
        return baseContainer.items
    }

    override fun resetQuickCraft() {
        resetDragMethod(baseContainer)
    }

    override fun clearContainer(playerIn: Player, inventoryIn: Container) {
        clearContainerMethod(baseContainer, playerIn, inventoryIn)
    }

    override fun broadcastChanges() {
        baseContainer.broadcastChanges()
    }

    override fun slotsChanged(inventoryIn: Container) {
        baseContainer.slotsChanged(inventoryIn)
    }

    override fun moveItemStackTo(stack: ItemStack, startIndex: Int, endIndex: Int, reverseDirection: Boolean): Boolean {
        return moveItemStackToMethod(baseContainer, stack, startIndex, endIndex, reverseDirection) as Boolean
    }
}