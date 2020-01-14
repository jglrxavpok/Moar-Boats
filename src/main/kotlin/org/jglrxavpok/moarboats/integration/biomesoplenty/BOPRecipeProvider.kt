package org.jglrxavpok.moarboats.integration.biomesoplenty

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.data.DataGenerator
import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.RecipeProvider
import net.minecraft.data.ShapelessRecipeBuilder
import net.minecraft.entity.item.BoatEntity
import net.minecraft.item.BoatItem
import net.minecraft.item.Item
import net.minecraft.tags.ItemTags
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.Tags
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.registries.GameData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.Items
import org.jglrxavpok.moarboats.common.items.UtilityBoatItem
import org.jglrxavpok.moarboats.datagen.GetBoatItemFromType
import org.jglrxavpok.moarboats.datagen.UtilityBoatType2Block
import java.util.function.Consumer

class BOPRecipeProvider(generator: DataGenerator): RecipeProvider(generator) {

    override fun registerRecipes(consumer: Consumer<IFinishedRecipe>) {
        // todo
        for(item in Items.list) {
            if(item is UtilityBoatItem) {
                registerRecipe(consumer, item)
            }
        }
    }

    private fun registerRecipe(consumer: Consumer<IFinishedRecipe>, item: UtilityBoatItem) {
        // todo
        MoarBoats.logger.info("Generating recipe for item ${item.registryName}")
        ShapelessRecipeBuilder.shapelessRecipe(item)
                .setGroup("moarboats:utility_boat_${item.containerType}")
                .addIngredient(GetBoatItemFromType(item.woodType)!!)
                .addIngredient(UtilityBoatType2Block(item.containerType))
                .addCriterion("in_water", this.enteredBlock(Blocks.WATER))
                .build(consumer)
    }
}