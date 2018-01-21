package org.jglrxavpok.moarboats.common.modules.inventories

import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

class ChestModuleInventory(boat: IControllable, module: BoatModule): BaseModuleInventory(3*9, "chest", boat, module) {
    override fun id2key(id: Int): String? = null

    override fun getFieldCount(): Int {
        return 0
    }
}