package org.jglrxavpok.moarboats.common.modules.inventories

import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

class SimpleModuleInventory(slotCount: Int, inventoryName: String, override val boat: IControllable, override val module: BoatModule): BaseModuleInventory(slotCount, inventoryName, boat, module) {
    override fun id2key(id: Int): String? {
        return null
    }

    override fun getFieldCount(): Int {
        return 0
    }
}