package org.jglrxavpok.moarboats.datagen

import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.advancements.critereon.EnterBlockTrigger
import net.minecraft.data.*
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.items.*
import java.util.function.Consumer

class UtilityBoatRecipes(generator: DataGenerator): RecipeProvider(generator) {

    override fun buildCraftingRecipes(consumer: Consumer<FinishedRecipe>) {
        for(type in BoatType.values()) {
            registerRecipe(consumer, BlastFurnaceBoatItem(type))
            registerRecipe(consumer, CartographyTableBoatItem(type))
            registerRecipe(consumer, ChestBoatItem(type))
            registerRecipe(consumer, CraftingTableBoatItem(type))
            registerRecipe(consumer, EnderChestBoatItem(type))
            registerRecipe(consumer, FurnaceBoatItem(type))
            registerRecipe(consumer, GrindstoneBoatItem(type))
            registerRecipe(consumer, JukeboxBoatItem(type))
            registerRecipe(consumer, LoomBoatItem(type))
            registerRecipe(consumer, ShulkerBoatItem(type))
            registerRecipe(consumer, SmokerBoatItem(type))
            registerRecipe(consumer, StonecutterBoatItem(type))
        }
    }

    private fun registerRecipe(consumer: Consumer<FinishedRecipe>, item: UtilityBoatItem) {
        MoarBoats.logger.info("Generating recipe for item ${ForgeRegistries.ITEMS.getKey(item)}")
        if(item is ShulkerBoatItem) {
            CustomRecipeBuilder(MBRecipeSerializers.ShulkerBoat.get(), item)
                    .save(consumer, item.boatType.getOriginModID()+":moarboats_${ForgeRegistries.ITEMS.getKey(item)!!.path}")
        } else {
            val baseBoat = item.boatType.provideBoatItem()
            ShapelessRecipeBuilder.shapeless(item)
                    .group("moarboats:utility_boat_${item.containerType}")
                    .requires(baseBoat)
                    .requires(UtilityBoatType2Block(item.containerType))
                    .unlockedBy("in_water", EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.WATER))
                    .save(consumer, ResourceLocation(item.boatType.getOriginModID(), "moarboats_${ForgeRegistries.ITEMS.getKey(item)!!.path}"))
        }
    }
}