package org.jglrxavpok.moarboats.datagen

import net.minecraft.data.DataGenerator
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.client.model.generators.ItemModelProvider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.BoatType

class JsonModelGenerator(generator: DataGenerator, val boatModID: String, val baseBoatModID: String, existingFile: ExistingFileHelper): ItemModelProvider(generator, MoarBoats.ModID, existingFile) {
    override fun getName(): String {
        return "Moar Boats BOP Json Auto-generated Models"
    }

    override fun registerModels() {
        // utility boats
        for (type in BoatType.values()) {
            if(type.getOriginModID() != boatModID)
                continue
            for(containerType in UtilityBoatTypeList) {
                val name = "${type.getFullName()}_${containerType}_boat"
                withExistingParent("${MoarBoats.ModID}:$name", ResourceLocation("item/generated"))
                        .texture("layer0", ResourceLocation("$baseBoatModID:item/${type.getShortName()}_boat"))
                        .texture("layer1", ResourceLocation("moarboats:items/utility/${containerType}"))
                        .texture("layer2", ResourceLocation("moarboats:items/utility/paddle"))
            }
        }

    }
}