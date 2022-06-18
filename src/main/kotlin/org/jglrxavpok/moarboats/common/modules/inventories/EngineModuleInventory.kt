package org.jglrxavpok.moarboats.common.modules.inventories

import net.minecraft.world.inventory.FurnaceFuelSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.FurnaceBlockEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

class EngineModuleInventory(boat: IControllable, module: BoatModule): BaseModuleInventory(1, "testEngine", boat, module) {
    override fun id2key(id: Int): String? = when(id) {
        0 -> "fuelTime"
        1 -> "fuelTotalTime"
        else -> null
    }

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean {
        if(index == 0) {
            val itemstack = list[0]
            return FurnaceBlockEntity.isFuel(stack) || FurnaceFuelSlot.isBucket(stack) && itemstack.item !== Items.BUCKET
        }
        return false
    }

    override fun getFieldCount(): Int {
        return 2
    }
}