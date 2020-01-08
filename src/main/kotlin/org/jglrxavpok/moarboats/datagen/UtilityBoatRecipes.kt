package org.jglrxavpok.moarboats.datagen

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
import java.util.function.Consumer

class UtilityBoatRecipes(generator: DataGenerator): RecipeProvider(generator) {

    private val boatCache = mutableMapOf<BoatEntity.Type, Item>()

    private fun buildBoatCache() {
        val typeField = ObfuscationReflectionHelper.findField(BoatItem::class.java, "field_185057_a")
        for(boatItem in GameData.getWrapper(Item::class.java).iterator()) {
            if(boatItem is BoatItem) {
                val boatType = typeField[boatItem] as BoatEntity.Type
                boatCache[boatType] = boatItem
                MoarBoats.logger.info("Boat ${boatItem.registryName} is of type $boatType")
            }
        }
    }

    override fun registerRecipes(consumer: Consumer<IFinishedRecipe>) {
        buildBoatCache()
        for(item in Items.list) {
            if(item is UtilityBoatItem) {
                registerRecipe(consumer, item)
            }
        }
    }

    private fun registerRecipe(consumer: Consumer<IFinishedRecipe>, item: UtilityBoatItem) {
        MoarBoats.logger.info("Generating recipe for item ${item.registryName}")
        ShapelessRecipeBuilder.shapelessRecipe(item)
                .setGroup("moarboats:utility_boat_${item.containerType}")
                .addIngredient(boatCache[item.woodType])
                .addIngredient(UtilityBoatType2Block(item.containerType))
                .addCriterion("in_water", this.enteredBlock(Blocks.WATER))
                .build(consumer)
    }
}