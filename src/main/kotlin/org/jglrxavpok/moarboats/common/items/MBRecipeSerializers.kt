package org.jglrxavpok.moarboats.common.items

import com.google.gson.JsonObject
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleRecipeSerializer

object MBRecipeSerializers {

    lateinit var MapWithPath: SimpleRecipeSerializer<MapWithPathRecipe>
    lateinit var BoatColoring: SimpleRecipeSerializer<ModularBoatColoringRecipe>
    lateinit var UpgradeToGoldenTicket: SimpleRecipeSerializer<UpgradeToGoldenTicketRecipe>
    lateinit var CopyGoldenTicket: SimpleRecipeSerializer<GoldenTicketCopyRecipe>
    lateinit var ShulkerBoat: SimpleRecipeSerializer<ShulkerBoatRecipe>
}