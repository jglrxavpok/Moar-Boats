package org.jglrxavpok.moarboats.datagen

import com.google.gson.JsonObject
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import java.util.function.Consumer

class CustomRecipeBuilder<T: Recipe<*>>(val recipeType: RecipeSerializer<T>, val resultItem: Item): RecipeBuilder {

    private val advancement = Advancement.Builder.advancement()
    private var group: String? = null

    override fun unlockedBy(s: String, i: CriterionTriggerInstance): RecipeBuilder {
        advancement.addCriterion(s, i)
        return this
    }

    override fun group(group: String?): RecipeBuilder {
        this.group = group
        return this
    }

    override fun getResult(): Item {
        return resultItem
    }

    override fun save(output: Consumer<FinishedRecipe>, location: ResourceLocation) {
        check(advancement.criteria.isNotEmpty()) { "No way of obtaining recipe $location" }
        output.accept(Result(location))
    }

    inner class Result(val key: ResourceLocation): FinishedRecipe {
        override fun serializeRecipeData(out: JsonObject) {
            out.addProperty("id", key.toString())
        }

        override fun getId(): ResourceLocation {
            return key
        }

        override fun getType(): RecipeSerializer<*> {
            return recipeType
        }

        override fun serializeAdvancement(): JsonObject? {
            return advancement.serializeToJson()
        }

        override fun getAdvancementId(): ResourceLocation? {
            return ResourceLocation(key.namespace, "recipes/" + resultItem.itemCategory!!.recipeFolderName + "/" + key.path)
        }

    }

}
