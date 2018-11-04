package org.jglrxavpok.moarboats.common.items

import net.minecraft.init.Items
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.oredict.DyeUtils
import net.minecraftforge.registries.IForgeRegistryEntry
import org.jglrxavpok.moarboats.MoarBoats

object MapWithPathRecipe: IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "map_with_path")
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width*height >= 4
    }

    override fun getRecipeOutput() = ItemStack.EMPTY

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
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
                DyeUtils.isDye(stack) -> {
                    if(DyeUtils.colorFromStack(stack).orElse(null) == EnumDyeColor.BLACK) {
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

        val mapID = "map_${filledMap.metadata}"
        return ItemMapWithPath.createStack(NBTTagList(), mapID, loops = false)
    }

    override fun matches(inv: InventoryCrafting, worldIn: World?): Boolean {
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
                DyeUtils.isDye(stack) -> {
                    if(DyeUtils.colorFromStack(stack).orElse(null) == EnumDyeColor.BLACK) {
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