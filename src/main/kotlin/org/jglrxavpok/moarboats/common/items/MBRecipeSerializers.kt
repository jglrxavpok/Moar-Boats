package org.jglrxavpok.moarboats.common.items

import com.google.gson.JsonObject
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleRecipeSerializer

object MBRecipeSerializers {

    lateinit var MapWithPath: SingletonSerializer<MapWithPathRecipe>
    lateinit var BoatColoring: SingletonSerializer<ModularBoatColoringRecipe>
    lateinit var UpgradeToGoldenTicket: SingletonSerializer<UpgradeToGoldenTicketRecipe>
    lateinit var CopyGoldenTicket: SingletonSerializer<GoldenTicketCopyRecipe>
    lateinit var ShulkerBoat: SimpleRecipeSerializer<ShulkerBoatRecipe>

    class SingletonSerializer<T: Recipe<CraftingContainer>>(val recipe: T): RecipeSerializer<T> {
        override fun toNetwork(buffer: FriendlyByteBuf, recipe: T) {

        }

        override fun fromJson(recipeId: ResourceLocation, json: JsonObject): T {
            return recipe
        }

        override fun fromNetwork(recipeId: ResourceLocation, buffer: FriendlyByteBuf): T {
            return recipe
        }

    }
}