package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Items
import net.minecraft.inventory.container.IContainerListener
import net.minecraft.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

class ContainerHelmModule(containerID: Int, playerInventory: PlayerInventory, helm: BoatModule, boat: IControllable): ContainerBoatModule<ContainerHelmModule>(HelmModule.containerType as ContainerType<ContainerHelmModule>, containerID, playerInventory, helm, boat) {

    val helmInventory = boat.getInventory(helm)

    init {
        this.addSlot(SlotMap(helmInventory, 0, 8, 8))

        addPlayerSlots(isLarge = true)
        this.addDataSlots(helmInventory.additionalData)
    }

    @OnlyIn(Dist.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.helmInventory.setField(id, data)
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.items[index]

        if (slot != null && !slot.isEmpty) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index != 0) {
                if (isMapItem(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 1..27) {
                    if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 28..36 && !this.moveItemStackTo(itemstack1, 1, 37, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemstack1, 1, 37, false)) {
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

    private fun isMapItem(itemStack: ItemStack): Boolean {
        return when(itemStack.item) {
            Items.FILLED_MAP -> true
            MapItemWithPath -> true
            ItemGoldenTicket -> true
            else -> false
        }
    }
}