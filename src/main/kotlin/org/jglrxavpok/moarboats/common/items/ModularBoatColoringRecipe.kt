package org.jglrxavpok.moarboats.common.items

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.oredict.DyeUtils
import net.minecraftforge.registries.ForgeRegistryEntry
import net.minecraftforge.registries.IForgeRegistryEntry
import org.jglrxavpok.moarboats.MoarBoats

object ModularBoatColoringRecipe: IRecipe {

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

    override fun getCraftingResult(inv: IInventory): ItemStack {
        var dyeColorIndex = -1
        var boatCount = 0
        var dyeCount = 0
        for(i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)
            if(DyeUtils.isDye(stack)) {
                val dyeColor = DyeUtils.colorFromStack(stack)
                if(dyeColor.isPresent) {
                    val color = dyeColor.get()
                    if(dyeColorIndex >= 0 && color.ordinal != dyeColorIndex) {
                        return ItemStack.EMPTY
                    }
                    dyeCount++
                    dyeColorIndex = color.ordinal
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
            return ItemStack(ModularBoatItem, 1, dyeColorIndex)
        }
        return ItemStack.EMPTY
    }

    override fun matches(inv: IInventory, worldIn: World?): Boolean {
        var dyeColorIndex = -1
        var boatCount = 0
        var dyeCount = 0
        for(i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)
            if(DyeUtils.isDye(stack)) {
                val dyeColor = DyeUtils.colorFromStack(stack)
                if(dyeColor.isPresent) {
                    val color = dyeColor.get()
                    if(dyeColorIndex >= 0 && color.ordinal != dyeColorIndex) {
                        return false
                    }
                    dyeCount++
                    dyeColorIndex = color.ordinal
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