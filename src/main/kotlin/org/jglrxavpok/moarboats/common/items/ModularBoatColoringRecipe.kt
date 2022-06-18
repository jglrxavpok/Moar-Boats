package org.jglrxavpok.moarboats.common.items

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import org.jglrxavpok.moarboats.MoarBoats

object ModularBoatColoringRecipe: CraftingRecipe {
    override fun getType(): RecipeType<*> {
        return RecipeType.CRAFTING
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "modular_boat_coloring")
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return MBRecipeSerializers.BoatColoring
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width*height >= 1+3
    }

    override fun getResultItem() = ItemStack.EMPTY

    override fun assemble(inv: CraftingContainer): ItemStack {
        var globalColor: DyeColor? = null
        var boatCount = 0
        var dyeCount = 0
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(DyeColor.getColor(stack) != null) {
                val dyeColor = DyeColor.getColor(stack)
                if(dyeColor != null) {
                    val color = dyeColor
                    if(globalColor != null && color != globalColor) {
                        return ItemStack.EMPTY
                    }
                    dyeCount++
                    globalColor = color
                } else {
                    return ItemStack.EMPTY
                }
            } else if(stack.item is ModularBoatItem) {
                boatCount++
            } else if(!stack.isEmpty) {
                return ItemStack.EMPTY
            }
        }
        if(boatCount == 1 && dyeCount == 3) {
            return ItemStack(ModularBoatItem[globalColor!!])
        }
        return ItemStack.EMPTY
    }

    override fun matches(inv: CraftingContainer, worldIn: Level?): Boolean {
        var globalColor: DyeColor? = null
        var boatCount = 0
        var dyeCount = 0
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(DyeColor.getColor(stack) != null) {
                val dyeColor = DyeColor.getColor(stack)
                if(dyeColor != null) {
                    val color = dyeColor
                    if(globalColor != null && color != globalColor) {
                        return false
                    }
                    dyeCount++
                    globalColor = color
                } else {
                    return false
                }
            } else if(stack.item is ModularBoatItem) {
                boatCount++
            } else if(!stack.isEmpty) {
                return false
            }
        }
        return boatCount == 1 && dyeCount == 3
    }
}