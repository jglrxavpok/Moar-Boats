package org.jglrxavpok.moarboats.common.modules.inventories

import net.minecraft.init.Items
import net.minecraft.inventory.SlotFurnaceFuel
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntityFurnace
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

class ChestModuleInventory(boat: IControllable, module: BoatModule): BaseModuleInventory(3*9, "chest", boat, module) {
    override fun id2key(id: Int): String? = null

    override fun getFieldCount(): Int {
        return 0
    }
}