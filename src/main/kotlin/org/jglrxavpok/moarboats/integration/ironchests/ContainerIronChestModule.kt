package org.jglrxavpok.moarboats.integration.ironchests

import com.progwml6.ironchest.common.blocks.ChestType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule

class ContainerIronChestModule(containerID: Int, playerInventory: PlayerInventory, module: BoatModule, boat: IControllable, val chestType: ChestType): ContainerBoatModule<ContainerIronChestModule>(module.containerType as ContainerType<ContainerIronChestModule>, containerID, playerInventory, module, boat) {
    val chestInventory = boat.getInventory(module)

    init {
        val numCols = chestType.rowLength
        val numRows = chestType.rowCount
        for (row in 0 until numRows) {
            for (column in 0 until numCols) {
                this.addSlot(Slot(chestInventory, column + row * numCols, 12 + column * 18, 18 + row * 18))
            }
        }

        addPlayerSlots(isLarge = false)
        this.trackIntArray(chestInventory.additionalData)
    }

    @OnlyIn(Dist.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.chestInventory.setField(id, data)
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
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