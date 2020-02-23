package org.jglrxavpok.moarboats.common.items

import com.google.gson.JsonObject
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.item.crafting.SpecialRecipeSerializer
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation

object MBRecipeSerializers {

    val MapWithPath = IRecipeSerializer.register("moarboats:map_with_path", SingletonSerializer(MapWithPathRecipe))
    val BoatColoring = IRecipeSerializer.register("moarboats:color_modular_boat", SingletonSerializer(ModularBoatColoringRecipe))
    val UpgradeToGoldenTicket = IRecipeSerializer.register("moarboats:upgrade_to_golden_ticket", SingletonSerializer(UpgradeToGoldenTicketRecipe))
    val CopyGoldenTicket = IRecipeSerializer.register("moarboats:copy_golden_ticket", SingletonSerializer(GoldenTicketCopyRecipe))
    val ShulkerBoat = IRecipeSerializer.register("moarboats:shulker_boat", SpecialRecipeSerializer { _ -> ShulkerBoatRecipe })

    class SingletonSerializer<T: IRecipe<CraftingInventory>>(val recipe: T): IRecipeSerializer<T> {
        override fun getRegistryName(): ResourceLocation? {
            return recipe.id
        }

        override fun getRegistryType(): Class<IRecipeSerializer<*>> {
            return this.javaClass
        }

        override fun setRegistryName(name: ResourceLocation?): IRecipeSerializer<*> {
            return this
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