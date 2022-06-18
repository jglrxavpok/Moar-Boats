package org.jglrxavpok.moarboats.common.items

import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.LoopingOptions

object MapWithPathRecipe: CraftingRecipe {

    override fun getType(): RecipeType<*> {
        return RecipeType.CRAFTING
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "map_with_path")
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return MBRecipeSerializers.MapWithPath
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width*height >= 4
    }

    override fun getResultItem() = ItemStack.EMPTY

    override fun assemble(inv: CraftingContainer): ItemStack {
        var featherCount = 0
        var filledMap: ItemStack? = null
        var blackDyeCount = 0
        var paperCount = 0
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            when {
                stack.item == Items.FILLED_MAP -> {
                    filledMap = stack
                }
                stack.item == Items.PAPER -> paperCount++
                DyeColor.getColor(stack) != null -> {
                    if(DyeColor.getColor(stack) == DyeColor.BLACK) {
                        blackDyeCount++
                    } else {
                        return ItemStack.EMPTY // invalid dye
                    }
                }
                stack.item == Items.FEATHER -> featherCount++
                stack.item == Items.AIR -> {}
                else -> return ItemStack.EMPTY // invalid item
            }
        }

        if(filledMap == null || featherCount != 1 || paperCount != 1 || blackDyeCount != 1)
            return ItemStack.EMPTY

        val mapID = "map_${filledMap.damageValue}"
        return MapItemWithPath.createStack(ListTag(), mapID, LoopingOptions.NoLoop)
    }

    override fun matches(inv: CraftingContainer, worldIn: Level?): Boolean {
        var featherCount = 0
        var filledMap: ItemStack? = null
        var blackDyeCount = 0
        var paperCount = 0
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            when {
                stack.item == Items.FILLED_MAP -> {
                    filledMap = stack
                }
                stack.item == Items.PAPER -> paperCount++
                DyeColor.getColor(stack) != null -> {
                    if(DyeColor.getColor(stack) == DyeColor.BLACK) {
                        blackDyeCount++
                    } else {
                        return false // invalid dye
                    }
                }
                stack.item == Items.FEATHER -> featherCount++
                stack.item == Items.AIR -> {}
                else -> return false // invalid item
            }

        }

        return !(filledMap == null || featherCount != 1 || paperCount != 1 || blackDyeCount != 1)
    }
}