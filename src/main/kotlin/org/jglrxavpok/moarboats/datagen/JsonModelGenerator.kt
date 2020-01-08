package org.jglrxavpok.moarboats.datagen

import com.google.gson.Gson
import net.minecraft.data.DataGenerator
import net.minecraft.data.IDataProvider
import net.minecraft.entity.item.BoatEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.client.model.generators.ItemModelProvider
import org.jglrxavpok.moarboats.MoarBoats

class JsonModelGenerator(generator: DataGenerator, modid: String, existingFile: ExistingFileHelper): ItemModelProvider(generator, modid, existingFile) {
    override fun getName(): String {
        return "Moar Boats Json Auto-generated Models"
    }

    override fun registerModels() {
        val gson = Gson()
        val outputFolder = generator.outputFolder

        // utility boats
        for (type in BoatEntity.Type.values()) {
            for(containerType in UtilityBoatTypeList) {
                val name = "${type.getName()}_${containerType}_boat"
                withExistingParent(name, ResourceLocation("item/generated"))
                        .texture("layer0", ResourceLocation("minecraft:item/${type.getName()}_boat"))
                        .texture("layer1", ResourceLocation("moarboats:items/utility/${containerType}"))
                        .texture("layer2", ResourceLocation("moarboats:items/utility/paddle"))
            }
        }

    }
}