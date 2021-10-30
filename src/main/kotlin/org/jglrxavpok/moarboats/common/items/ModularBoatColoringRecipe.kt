package org.jglrxavpok.moarboats.common.items

import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.DyeColor
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.ICraftingRecipe
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats

object ModularBoatColoringRecipe: ICraftingRecipe {
    override fun getType(): IRecipeType<*> {
        return IRecipeType.CRAFTING
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "modular_boat_coloring")
    }

    override fun getSerializer(): IRecipeSerializer<*> {
        return MBRecipeSerializers.BoatColoring
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width*height >= 1+3
    }

    override fun getRecipeOutput() = ItemStack.EMPTY

    override fun getCraftingResult(inv: CraftingInventory): ItemStack {
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

    override fun matches(inv: CraftingInventory, worldIn: World?): Boolean {
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