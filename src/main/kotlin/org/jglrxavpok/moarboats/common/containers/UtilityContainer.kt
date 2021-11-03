package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.*
import net.minecraft.item.ItemStack
import net.minecraft.util.IIntArray
import net.minecraft.util.IntReferenceHolder
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.common.ObfuscationReflectionHelper

class UtilityContainer<T: Container>(type: ContainerType<T>, id: Int, val baseContainer: Container): Container(type, id) {

    companion object {
        val resetDragMethod = ObfuscationReflectionHelper.findMethod(Container::class.java, "func_94533_d")
        val clearContainerMethod = ObfuscationReflectionHelper.findMethod(Container::class.java, "func_193327_a", PlayerEntity::class.java, World::class.java, IInventory::class.java)
        val moveItemStackToMethod = ObfuscationReflectionHelper.findMethod(Container::class.java, "func_75135_a", ItemStack::class.java, Integer.TYPE, Integer.TYPE, java.lang.Boolean.TYPE)
    }

    override fun stillValid(playerIn: PlayerEntity): Boolean {
        return true
    }

    override fun canDragTo(slotIn: Slot): Boolean {
        return baseContainer.canDragTo(slotIn)
    }

    override fun canTakeItemForPickAll(stack: ItemStack, slotIn: Slot): Boolean {
        return baseContainer.canTakeItemForPickAll(stack, slotIn)
    }

    override fun setAll(p_190896_1_: MutableList<ItemStack>) {
        baseContainer.setAll(p_190896_1_)
    }

    override fun quickMoveStack(playerIn: PlayerEntity, index: Int): ItemStack {
        return baseContainer.quickMoveStack(playerIn, index)
    }

    override fun addSlotListener(listener: IContainerListener) {
        baseContainer.addSlotListener(listener)
    }

    override fun setSynched(player: PlayerEntity, canCraft: Boolean) {
        baseContainer.setSynched(player, canCraft)
    }

    override fun clickMenuButton(playerIn: PlayerEntity, id: Int): Boolean {
        return baseContainer.clickMenuButton(playerIn, id)
    }

    override fun removeSlotListener(listener: IContainerListener) {
        baseContainer.removeSlotListener(listener)
    }

    override fun setData(id: Int, data: Int) {
        baseContainer.setData(id, data)
    }

    override fun removed(playerIn: PlayerEntity) {
        baseContainer.removed(playerIn)
    }

    override fun setItem(slotID: Int, stack: ItemStack) {
        baseContainer.setItem(slotID, stack)
    }

    override fun clicked(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: PlayerEntity): ItemStack {
        return baseContainer.clicked(slotId, dragType, clickTypeIn, player)
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

    override fun backup(invPlayer: PlayerInventory): Short {
        return baseContainer.backup(invPlayer)
    }

    override fun clearContainer(playerIn: PlayerEntity, worldIn: World, inventoryIn: IInventory) {
        clearContainerMethod(baseContainer, playerIn, worldIn, inventoryIn)
    }

    override fun isSynched(player: PlayerEntity): Boolean {
        return baseContainer.isSynched(player)
    }

    override fun broadcastChanges() {
        baseContainer.broadcastChanges()
    }

    override fun slotsChanged(inventoryIn: IInventory) {
        baseContainer.slotsChanged(inventoryIn)
    }

    override fun moveItemStackTo(stack: ItemStack, startIndex: Int, endIndex: Int, reverseDirection: Boolean): Boolean {
        return moveItemStackToMethod(baseContainer, stack, startIndex, endIndex, reverseDirection) as Boolean
    }
}