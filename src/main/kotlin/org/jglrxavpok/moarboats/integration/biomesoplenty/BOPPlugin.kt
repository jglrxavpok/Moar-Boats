package org.jglrxavpok.moarboats.integration.biomesoplenty

import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent
import org.jglrxavpok.moarboats.datagen.JsonModelGenerator
import org.jglrxavpok.moarboats.integration.MoarBoatsIntegration
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin

@MoarBoatsIntegration("biomesoplenty")
class BOPPlugin: MoarBoatsPlugin {

    override fun registerProviders(event: GatherDataEvent, generator: DataGenerator, existingFileHelper: ExistingFileHelper) {
        generator.addProvider(JsonModelGenerator(generator, "biomesoplenty", existingFileHelper))
        generator.addProvider(BOPRecipeProvider(generator))
    }
}