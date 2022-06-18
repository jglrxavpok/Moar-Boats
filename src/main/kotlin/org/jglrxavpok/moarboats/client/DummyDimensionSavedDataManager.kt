package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.datafix.DataFixesManager
import net.minecraft.world.level.storage.DimensionDataStorage
import net.minecraft.world.storage.WorldSavedData
import java.io.File
import java.util.function.Supplier

object DummyDimensionSavedDataManager: DimensionDataStorage(File(".moarboats_fake_save_manager"), DataFixerBuilder.getDataFixer()) {

    private val savedData = mutableMapOf<String, WorldSavedData>()

    override fun <T : WorldSavedData?> get(defaultSupplier: Supplier<T>, name: String): T? {
        if(name.startsWith("map_")) {
            return Minecraft.getInstance().level?.getMapData(name) as? T
        }
        return savedData[name] as T
    }

    override fun set(data: WorldSavedData) {
        savedData[data.id] = data
    }

    override fun readTagFromDisk(name: String, worldVersion: Int): CompoundTag {
        return CompoundTag()
    }

    override fun save() {
        // don't
    }
}