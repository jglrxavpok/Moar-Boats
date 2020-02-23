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
        val mergeItemStackMethod = ObfuscationReflectionHelper.findMethod(Container::class.java, "func_75135_a", ItemStack::class.java, Integer.TYPE, Integer.TYPE, java.lang.Boolean.TYPE)
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }

    override fun canDragIntoSlot(slotIn: Slot): Boolean {
        return baseContainer.canDragIntoSlot(slotIn)
    }

    override fun canMergeSlot(stack: ItemStack, slotIn: Slot): Boolean {
        return baseContainer.canMergeSlot(stack, slotIn)
    }

    override fun setAll(p_190896_1_: MutableList<ItemStack>) {
        baseContainer.setAll(p_190896_1_)
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        return baseContainer.transferStackInSlot(playerIn, index)
    }

    override fun addListener(listener: IContainerListener) {
        baseContainer.addListener(listener)
    }

    override fun setCanCraft(player: PlayerEntity, canCraft: Boolean) {
        baseContainer.setCanCraft(player, canCraft)
    }

    override fun enchantItem(playerIn: PlayerEntity, id: Int): Boolean {
        return baseContainer.enchantItem(playerIn, id)
    }

    override fun removeListener(listener: IContainerListener) {
        baseContainer.removeListener(listener)
    }

    override fun updateProgressBar(id: Int, data: Int) {
        baseContainer.updateProgressBar(id, data)
    }

    override fun onContainerClosed(playerIn: PlayerEntity) {
        baseContainer.onContainerClosed(playerIn)
    }

    override fun putStackInSlot(slotID: Int, stack: ItemStack) {
        baseContainer.putStackInSlot(slotID, stack)
    }

    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: PlayerEntity): ItemStack {
        return baseContainer.slotClick(slotId, dragType, clickTypeIn, player)
    }

    override fun getSlot(slotId: Int): Slot {
        return baseContainer.getSlot(slotId)
    }

    override fun getInventory(): NonNullList<ItemStack> {
        return baseContainer.getInventory()
    }

    override fun resetDrag() {
        resetDragMethod(baseContainer)
    }

    override fun getNextTransactionID(invPlayer: PlayerInventory): Short {
        return baseContainer.getNextTransactionID(invPlayer)
    }

    override fun clearContainer(playerIn: PlayerEntity, worldIn: World, inventoryIn: IInventory) {
        clearContainerMethod(baseContainer, playerIn, worldIn, inventoryIn)
    }

    override fun getCanCraft(player: PlayerEntity): Boolean {
        return baseContainer.getCanCraft(player)
    }

    override fun detectAndSendChanges() {
        baseContainer.detectAndSendChanges()
    }

    override fun onCraftMatrixChanged(inventoryIn: IInventory) {
        baseContainer.onCraftMatrixChanged(inventoryIn)
    }

    override fun mergeItemStack(stack: ItemStack, startIndex: Int, endIndex: Int, reverseDirection: Boolean): Boolean {
        return mergeItemStackMethod(baseContainer, stack, startIndex, endIndex, reverseDirection) as Boolean
    }
}