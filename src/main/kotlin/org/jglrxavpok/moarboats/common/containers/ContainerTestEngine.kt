package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.inventory.SlotFurnaceFuel
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

class ContainerTestEngine(val playerInventory: InventoryPlayer, val engine: BoatModule, val boat: IControllable): Container() {

    val engineInventory = boat.getInventory(engine)
    private var fuelTime = 0
    private var fuelTotalTime = 0

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

    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.engineInventory.setField(id, data)
    }

}