package org.jglrxavpok.moarboats.common.items

import net.minecraft.item.Items
import net.minecraft.inventory.IInventory
import net.minecraft.item.DyeColor
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.nbt.ListNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.LoopingOptions

object MapWithPathRecipe: IRecipe<IInventory> {

    override fun getType(): IRecipeType<*> {
        return IRecipeType.CRAFTING
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "map_with_path")
    }

    override fun getSerializer(): IRecipeSerializer<*> {
        return MBRecipeSerializers.MapWithPath
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width*height >= 4
    }

    override fun getRecipeOutput() = ItemStack.EMPTY

    override fun getCraftingResult(inv: IInventory): ItemStack {
        var featherCount = 0
        var filledMap: ItemStack? = null
        var blackDyeCount = 0
        var paperCount = 0
        for(i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)
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

        val mapID = "map_${filledMap.damage}"
        return MapItemWithPath.createStack(ListNBT(), mapID, LoopingOptions.NoLoop)
    }

    override fun matches(inv: IInventory, worldIn: World?): Boolean {
        var featherCount = 0
        var filledMap: ItemStack? = null
        var blackDyeCount = 0
        var paperCount = 0
        for(i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)
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