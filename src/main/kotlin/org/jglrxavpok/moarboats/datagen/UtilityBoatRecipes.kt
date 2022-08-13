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
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.items.*
import java.util.function.Consumer

class UtilityBoatRecipes(generator: DataGenerator): RecipeProvider(generator) {

    override fun buildCraftingRecipes(consumer: Consumer<FinishedRecipe>) {
        for(type in BoatType.values()) {
            registerRecipe(consumer, MBItems.BlastFurnaceBoats[type]!!.get())
            registerRecipe(consumer, MBItems.CartographyTableBoats[type]!!.get())
            registerRecipe(consumer, MBItems.CraftingTableBoats[type]!!.get())
            registerRecipe(consumer, MBItems.EnderChestBoats[type]!!.get())
            registerRecipe(consumer, MBItems.FurnaceBoats[type]!!.get())
            registerRecipe(consumer, MBItems.GrindstoneBoats[type]!!.get())
            registerRecipe(consumer, MBItems.JukeboxBoats[type]!!.get())
            registerRecipe(consumer, MBItems.LoomBoats[type]!!.get())
            registerRecipe(consumer, MBItems.ShulkerBoats[type]!!.get())
            registerRecipe(consumer, MBItems.SmokerBoats[type]!!.get())
            registerRecipe(consumer, MBItems.StonecutterBoats[type]!!.get())
        }
    }

    private fun registerRecipe(consumer: Consumer<FinishedRecipe>, item: UtilityBoatItem) {
        val recipeName = "moarboats_${ForgeRegistries.ITEMS.getKey(item)!!.path}"
        MoarBoats.logger.info("Generating recipe '$recipeName' for item $item")
        if(item is ShulkerBoatItem) {
            CustomRecipeBuilder(MBRecipeSerializers.ShulkerBoat.get(), item)
                    .unlockedBy("in_water", EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.WATER))
                    .save(consumer, item.boatType.getOriginModID()+":$recipeName")
        } else {
            val baseBoat = item.boatType.provideBoatItem()
            ShapelessRecipeBuilder.shapeless(item)
                    .group("moarboats:utility_boat_${item.containerType}")
                    .requires(baseBoat)
                    .requires(UtilityBoatType2Block(item.containerType))
                    .unlockedBy("in_water", EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.WATER))
                    .save(consumer, ResourceLocation(item.boatType.getOriginModID(), recipeName))
        }
    }
}