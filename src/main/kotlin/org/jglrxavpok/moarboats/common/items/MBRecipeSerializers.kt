package org.jglrxavpok.moarboats.common.items

import com.google.gson.JsonObject
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.item.crafting.RecipeSerializers
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation

object MBRecipeSerializers {

    val MapWithPath = RecipeSerializers.register(SingletonSerializer(MapWithPathRecipe))
    val BoatColoring = RecipeSerializers.register(SingletonSerializer(ModularBoatColoringRecipe))
    val UpgradeToGoldenTicket = RecipeSerializers.register(SingletonSerializer(UpgradeToGoldenTicketRecipe))
    val CopyGoldenTicket = RecipeSerializers.register(SingletonSerializer(GoldenTicketCopyRecipe))

    class SingletonSerializer<T: IRecipe>(val recipe: T): IRecipeSerializer<T> {
        override fun getName(): ResourceLocation {
            return recipe.id
        }

        override fun write(buffer: PacketBuffer, recipe: T) {

        }

        override fun read(recipeId: ResourceLocation, json: JsonObject): T {
            return recipe
        }

        override fun read(recipeId: ResourceLocation, buffer: PacketBuffer): T {
            return recipe
        }

    }
}