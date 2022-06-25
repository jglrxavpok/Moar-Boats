package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.DimensionDataStorage
import org.jglrxavpok.moarboats.Proxy

class ClientProxy: Proxy {
    override fun get(dimensionType: ResourceKey<Level>): DimensionDataStorage {
        return when {
            Minecraft.getInstance().singleplayerServer != null /* LAN */ -> Minecraft.getInstance().singleplayerServer!!.getLevel(dimensionType)?.dataStorage
                ?: error("Tried to get save data of an nonexistent dimension type? $dimensionType")
            else -> DummyDimensionSavedDataManager
        }
    }
}