package org.jglrxavpok.moarboats.integration.ironchests

import com.progwml6.ironchest.common.block.IronChestsTypes
import com.progwml6.ironchest.common.inventory.DirtChestSlot
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

class ContainerIronChestModule(containerID: Int, playerInventory: PlayerInventory, module: BoatModule, boat: IControllable, val chestType: IronChestsTypes): ContainerBoatModule<ContainerIronChestModule>(module.containerType as ContainerType<ContainerIronChestModule>, containerID, playerInventory, module, boat) {
    val chestInventory = boat.getInventory(module)

    init {
        if (chestType == IronChestsTypes.DIRT) {
            addSlot(DirtChestSlot(chestInventory, 0, 12 + 4 * 18, 8 + 2 * 18))
        } else {
            val numCols = chestType.rowLength
            val numRows = chestType.rowCount
            for (row in 0 until numRows) {
                for (column in 0 until numCols) {
                    this.addSlot(Slot(chestInventory, column + row * numCols, 12 + column * 18, 18 + row * 18))
                }
            }
        }

        val xStart = (chestType.xSize - 162) / 2 + 1

        for (row in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(playerInventory, j + row * 9 + 9, xStart + j * 18, chestType.ySize - (4 - row) * 18 - 10))
            }
        }

        for (k in 0..8) {
            this.addSlot(Slot(playerInventory, k, xStart + k * 18, chestType.ySize - 24))
        }
        this.addDataSlots(chestInventory.additionalData)
    }

    @OnlyIn(Dist.CLIENT)
    override fun setData(id: Int, data: Int) {
        this.chestInventory.setField(id, data)
    }

    override fun quickMoveStack(playerIn: PlayerEntity, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.slots[index]

        if (slot != null && slot.hasItem()) {
            val itemstack1 = slot.item
            itemstack = itemstack1.copy()

            if (index < 3 * 9) {
                if (!this.moveItemStackTo(itemstack1, 3 * 9, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 3 * 9, false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.isEmpty) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }

        return itemstack
    }

}