package org.jglrxavpok.moarboats.server

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.DimensionDataStorage
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.Proxy

class ServerProxy: Proxy {

    override fun showDetailedTooltip(): Boolean {
        return false
    }

    override fun get(dimensionType: ResourceKey<Level>): DimensionDataStorage {
        return MoarBoats.dedicatedServerInstance!!.getLevel(dimensionType)?.dataStorage ?: error("Tried to get save data of an nonexistent dimension type? $dimensionType")
    }
}