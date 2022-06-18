package org.jglrxavpok.moarboats.datagen

import net.minecraft.advancements.critereon.EnterBlockTrigger
import net.minecraft.data.*
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.Items
import org.jglrxavpok.moarboats.common.items.MBRecipeSerializers
import org.jglrxavpok.moarboats.common.items.UtilityBoatItem
import java.util.function.Consumer

class UtilityBoatRecipes(generator: DataGenerator): RecipeProvider(generator) {

    override fun buildCraftingRecipes(consumer: Consumer<FinishedRecipe>) {
        for(item in Items.list) {
            if(item is UtilityBoatItem) {
                registerRecipe(consumer, item)
            }
        }
    }

    private fun registerRecipe(consumer: Consumer<FinishedRecipe>, item: UtilityBoatItem) {
        MoarBoats.logger.info("Generating recipe for item ${item.registryName}")
        if(item.containerType == "shulker") {
            CustomRecipeBuilder.special(MBRecipeSerializers.ShulkerBoat)
                    .save(consumer, item.boatType.getOriginModID()+":moarboats_${item.registryName!!.path}")
        } else {
            val baseBoat = item.boatType.provideBoatItem()
            ShapelessRecipeBuilder.shapeless(item)
                    .group("moarboats:utility_boat_${item.containerType}")
                    .requires(baseBoat)
                    .requires(UtilityBoatType2Block(item.containerType))
                    .unlockedBy("in_water", EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.WATER))
                    .save(consumer, ResourceLocation(item.boatType.getOriginModID(), "moarboats_${item.registryName!!.path}"))
        }
    }
}