package org.jglrxavpok.moarboats.api

import net.minecraftforge.eventbus.api.Event

class ModuleRegistryEvent(val entry: BoatModuleEntry): Event() {

    val module = entry.module
    val item = entry.correspondingItem

    override fun isCancelable(): Boolean {
        return true
    }
}
