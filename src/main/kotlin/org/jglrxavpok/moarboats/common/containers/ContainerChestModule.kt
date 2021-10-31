package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.ChestModule

class ContainerChestModule(containerID: Int, playerInventory: PlayerInventory, engine: BoatModule, boat: IControllable): ContainerBoatModule<ContainerChestModule>(ChestModule.containerType as ContainerType<ContainerChestModule>, containerID, playerInventory, engine, boat) {

    val chestInventory = boat.getInventory(engine)

    init {
        val numRows = 3
        for (j in 0 until numRows) {
            for (k in 0..8) {
                this.addSlot(Slot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18 -2))
            }
        }

        addPlayerSlots(isLarge = false)
        this.addDataSlots(chestInventory.additionalData)
    }

    @OnlyIn(Dist.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.chestInventory.setField(id, data)
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.items[index]

        if (slot != null && !slot.isEmpty) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index < 3 * 9) {
                if (!this.moveItemStackTo(itemstack1, 3 * 9, this.items.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 3 * 9, false)) {
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