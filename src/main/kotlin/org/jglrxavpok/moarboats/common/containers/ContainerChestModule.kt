package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.ChestModule

class ContainerChestModule(menuType: MenuType<ContainerChestModule>, containerID: Int, playerInventory: Inventory, engine: BoatModule, boat: IControllable): ContainerBoatModule<ContainerChestModule>(
    menuType, containerID, playerInventory, boat) {

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
    override fun setData(id: Int, data: Int) {
        this.chestInventory.setField(id, data)
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
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