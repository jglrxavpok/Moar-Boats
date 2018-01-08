package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

class ContainerTestEngine(val playerInventory: InventoryPlayer, val engine: BoatModule, val boat: IControllable): Container() {

    val engineInventory = boat.getInventory(engine)
    private var fuelTime = engineInventory.getField(0)
    private var fuelTotalTime = engineInventory.getField(1)

    init {
        this.addSlotToContainer(SlotFurnaceFuel(engineInventory, 0, 56, 53))

        addPlayerSlots()
    }

    private fun addPlayerSlots() {
        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlotToContainer(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        for (k in 0..8) {
            this.addSlotToContainer(Slot(playerInventory, k, 8 + k * 18, 142))
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        listener.sendAllWindowProperties(this, engineInventory)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        for(listener in listeners) {
            if (this.fuelTotalTime != this.engineInventory.getField(0)) {
                listener.sendWindowProperty(this, 0, this.engineInventory.getField(0))
            }

            if (this.fuelTime != this.engineInventory.getField(1)) {
                listener.sendWindowProperty(this, 1, this.engineInventory.getField(1))
            }
        }

        this.fuelTime = this.engineInventory.getField(0)
        this.fuelTotalTime = this.engineInventory.getField(1)
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.engineInventory.setField(id, data)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index != 0) {
                if (TileEntityFurnace.isItemFuel(itemstack1)) {
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
}