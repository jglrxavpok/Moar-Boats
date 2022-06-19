package org.jglrxavpok.moarboats.common.items

import com.google.gson.JsonObject
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleRecipeSerializer
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats

object MBRecipeSerializers {

    val Registry = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MoarBoats.ModID)

    val MapWithPath = Registry.register("map_with_path") {
        SimpleRecipeSerializer { loc -> MapWithPathRecipe }
    }

    val BoatColoring = Registry.register("modular_boat_coloring") {
        SimpleRecipeSerializer { loc -> ModularBoatColoringRecipe }
    }

    val UpgradeToGoldenTicket = Registry.register("update_to_golden_ticket") {
        SimpleRecipeSerializer { loc -> UpgradeToGoldenTicketRecipe }
    }

    val CopyGoldenTicket = Registry.register("copy_golden_ticket") {
        SimpleRecipeSerializer { loc -> GoldenTicketCopyRecipe }
    }

    val ShulkerBoat = Registry.register("shulker_boat") {
        SimpleRecipeSerializer { loc -> ShulkerBoatRecipe }
    }
}