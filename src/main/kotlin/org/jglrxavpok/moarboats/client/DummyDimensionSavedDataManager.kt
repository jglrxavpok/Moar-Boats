package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.storage.DimensionDataStorage
import java.io.File
import java.util.function.Function
import java.util.function.Supplier

object DummyDimensionSavedDataManager: DimensionDataStorage(File(".moarboats_fake_save_manager"), DataFixers.getDataFixer()) {

    private val savedData = mutableMapOf<String, SavedData>()

    override fun <T : SavedData?> get(p_164859_: Function<CompoundTag, T>, name: String): T? {
        if(name.startsWith("map_")) {
            return Minecraft.getInstance().level?.getMapData(name) as? T
        }
        return savedData[name] as T
    }

    override fun set(id: String, data: SavedData) {
        savedData[id] = data
    }

    override fun readTagFromDisk(name: String, worldVersion: Int): CompoundTag {
        return CompoundTag()
    }

    override fun save() {
        // don't
    }
}