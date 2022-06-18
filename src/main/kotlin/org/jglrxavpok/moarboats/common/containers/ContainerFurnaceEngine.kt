package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.FurnaceBlockEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.FurnaceEngineModule

class ContainerFurnaceEngine(containerID: Int, playerInventory: Inventory, engine: BoatModule, boat: IControllable): ContainerBoatModule<ContainerFurnaceEngine>(FurnaceEngineModule.containerType as MenuType<ContainerFurnaceEngine>, containerID, playerInventory, engine, boat) {

    val engineInventory = boat.getInventory(engine)
    private var fuelTime = engineInventory.getField(0)
    private var fuelTotalTime = engineInventory.getField(1)

    init {
        this.addSlot(SlotEngineFuel(engineInventory, 0, 8, 8))

        addPlayerSlots(isLarge = true)
        this.addDataSlots(engineInventory.additionalData)
    }

    override fun broadcastChanges() {
        super.broadcastChanges()

        for(listener in getListeners(this)) {
            if (this.fuelTotalTime != this.engineInventory.getField(0)) {
                listener.dataChanged(this, 0, this.engineInventory.getField(0))
            }

            if (this.fuelTime != this.engineInventory.getField(1)) {
                listener.dataChanged(this, 1, this.engineInventory.getField(1))
            }
        }

        this.fuelTime = this.engineInventory.getField(0)
        this.fuelTotalTime = this.engineInventory.getField(1)
    }

    @OnlyIn(Dist.CLIENT)
    override fun setData(id: Int, data: Int) {
        this.engineInventory.setField(id, data)
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.slots[index]

        if (slot != null && slot.hasItem()) {
            val itemstack1 = slot.item
            itemstack = itemstack1.copy()

            if (index != 0) {
                if (FurnaceBlockEntity.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 1..28) {
                    if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 28..36 && !this.moveItemStackTo(itemstack1, 1, 27, false)) {
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


    class SlotEngineFuel(inventoryIn: Container, slotIndex: Int, xPosition: Int, yPosition: Int) : Slot(inventoryIn, slotIndex, xPosition, yPosition) {

        override fun mayPlace(stack: ItemStack): Boolean {
            return FurnaceEngineModule.isItemFuel(stack) || isBucket(stack)
        }

        override fun getMaxStackSize(stack: ItemStack): Int {
            return if (isBucket(stack)) 1 else super.getMaxStackSize(stack)
        }

        fun isBucket(stack: ItemStack): Boolean {
            return stack.item === Items.BUCKET
        }
    }
}