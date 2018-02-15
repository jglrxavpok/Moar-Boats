package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.Items
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

class ContainerFishingModule(playerInventory: InventoryPlayer, val fishingModule: BoatModule, val boat: IControllable): ContainerBase(playerInventory) {

    val fishingModuleInv = boat.getInventory(fishingModule)

    init {
        this.addSlotToContainer(SlotFishingRod(fishingModuleInv, 0, 80, 36))

        addPlayerSlots(isLarge = false)
    }

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        listener.sendAllWindowProperties(this, fishingModuleInv)
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.fishingModuleInv.setField(id, data)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index != 0) {
                if (isItemFishingRod(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 1..27) {
                    if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 28..36 && !this.mergeItemStack(itemstack1, 1, 37, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(itemstack1, 1, 37, false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.isEmpty) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (itemstack1.count == itemstack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(playerIn, itemstack1)
        }

        return itemstack
    }

    private fun isItemFishingRod(itemStack: ItemStack) = itemStack.item == Items.FISHING_ROD
}