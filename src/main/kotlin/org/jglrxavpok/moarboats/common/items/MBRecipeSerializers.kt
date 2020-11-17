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

    lateinit var MapWithPath: SingletonSerializer<MapWithPathRecipe>
    lateinit var BoatColoring: SingletonSerializer<ModularBoatColoringRecipe>
    lateinit var UpgradeToGoldenTicket: SingletonSerializer<UpgradeToGoldenTicketRecipe>
    lateinit var CopyGoldenTicket: SingletonSerializer<GoldenTicketCopyRecipe>
    lateinit var ShulkerBoat: SpecialRecipeSerializer<ShulkerBoatRecipe>

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