package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

class ContainerChestModule(playerInventory: InventoryPlayer, val engine: BoatModule, val boat: IControllable): ContainerBase(playerInventory) {

    val chestInventory = boat.getInventory(engine)

    init {
        val numRows = 3
        for (j in 0 until numRows) {
            for (k in 0..8) {
                this.addSlotToContainer(Slot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18 -2))
            }
        }

        addPlayerSlots(isLarge = false)
    }

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        listener.sendAllWindowProperties(this, chestInventory)
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.chestInventory.setField(id, data)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index < 3 * 9) {
                if (!this.mergeItemStack(itemstack1, 3 * 9, this.inventorySlots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 3 * 9, false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.isEmpty) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }
        }

        return itemstack
    }
}