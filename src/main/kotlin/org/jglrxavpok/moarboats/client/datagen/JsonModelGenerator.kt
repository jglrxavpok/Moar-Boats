package org.jglrxavpok.moarboats.client.datagen

import com.google.gson.Gson
import net.minecraft.data.DataGenerator
import net.minecraft.data.IDataProvider
import net.minecraft.entity.item.BoatEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.client.model.generators.ItemModelProvider
import org.jglrxavpok.moarboats.MoarBoats
import java.nio.file.Path


class JsonModelGenerator(generator: DataGenerator, modid: String, existingFile: ExistingFileHelper): ItemModelProvider(generator, modid, existingFile) {
    override fun getName(): String {
        return "Moar Boats Json Auto-generated Models"
    }

    override fun registerModels() {
        val gson = Gson()
        val outputFolder = generator.outputFolder

        // utility boats
        for (type in BoatEntity.Type.values()) {
            for(containerType in arrayOf("blast_furnace", "cartography_table", "chest", "crafting_table", "ender_chest", "furnace", "jukebox", "loom", "shulker", "smoker", "stonecutter")) {
                val name = "${type.getName()}_${containerType}_boat"
                val path = outputFolder.resolve("assets/$modid/models/item/$name.json")
                val json = withExistingParent(name, ResourceLocation("item/generated"))
                        .texture("layer0", ResourceLocation("minecraft:item/${type.getName()}_boat"))
                        .texture("layer1", ResourceLocation("moarboats:items/utility/${containerType}"))
                        .texture("layer2", ResourceLocation("moarboats:items/utility/paddle"))
                        .toJson()
                IDataProvider.save(gson, cache, json, path)
                MoarBoats.logger.info("Generated $path")
            }
        }

    }
}