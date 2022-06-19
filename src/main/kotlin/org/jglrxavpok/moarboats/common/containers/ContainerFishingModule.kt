package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

class ContainerFishingModule(menuType: MenuType<ContainerFishingModule>, containerID: Int, playerInventory: Inventory, fishingModule: BoatModule, boat: IControllable): ContainerBoatModule<ContainerFishingModule>(
    menuType, containerID, playerInventory, boat) {

    val fishingModuleInv = boat.getInventory(fishingModule)

    init {
        this.addSlot(SlotFishingRod(fishingModuleInv, 0, 80, 36))

        addPlayerSlots(isLarge = false)
        this.addDataSlots(fishingModuleInv.additionalData)
    }

    @OnlyIn(Dist.CLIENT)
    override fun setData(id: Int, data: Int) {
        this.fishingModuleInv.setField(id, data)
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.slots[index]

        if (slot != null && slot.hasItem()) {
            val itemstack1 = slot.item
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
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
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