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
import org.jglrxavpok.moarboats.common.modules.FishingModule

class ContainerFishingModule(containerID: Int, playerInventory: PlayerInventory, fishingModule: BoatModule, boat: IControllable): ContainerBoatModule<ContainerFishingModule>(FishingModule.containerType as ContainerType<ContainerFishingModule>, containerID, playerInventory, fishingModule, boat) {

    val fishingModuleInv = boat.getInventory(fishingModule)

    init {
        this.addSlot(SlotFishingRod(fishingModuleInv, 0, 80, 36))

        addPlayerSlots(isLarge = false)
        this.addDataSlots(fishingModuleInv.additionalData)
    }

    @OnlyIn(Dist.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.fishingModuleInv.setField(id, data)
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.items[index]

        if (slot != null && !slot.isEmpty) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index != 0) {
                if (isItemFishingRod(itemstack1)) {
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

    private fun isItemFishingRod(itemStack: ItemStack) = itemStack.item == Items.FISHING_ROD
}