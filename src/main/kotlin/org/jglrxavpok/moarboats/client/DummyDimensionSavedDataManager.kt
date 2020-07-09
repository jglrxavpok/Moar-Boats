package org.jglrxavpok.moarboats.client

import com.mojang.datafixers.DataFixerBuilder
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.datafix.DataFixesManager
import net.minecraft.world.storage.DimensionSavedDataManager
import net.minecraft.world.storage.WorldSavedData
import java.io.File
import java.util.function.Supplier

object DummyDimensionSavedDataManager: DimensionSavedDataManager(File(".moarboats_fake_save_manager"), DataFixesManager.getDataFixer()) {

    private val savedData = mutableMapOf<String, WorldSavedData>()

    override fun <T : WorldSavedData?> get(defaultSupplier: Supplier<T>, name: String): T? {
        if(name.startsWith("map_")) {
            return Minecraft.getInstance().world.getMapData(name) as? T
        }
        return savedData[name] as T
    }

    override fun set(data: WorldSavedData) {
        savedData[data.name] = data
    }

    override fun load(name: String, worldVersion: Int): CompoundNBT {
        return CompoundNBT()
    }

    override fun save() {
        // don't
    }
}