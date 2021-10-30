package org.jglrxavpok.moarboats.common.items

import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.crafting.*
import net.minecraft.tileentity.ShulkerBoxTileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.datafix.fixes.ShulkerBoxItemColor
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.BoatType

object ShulkerBoatRecipe: ICraftingRecipe {
    override fun getType(): IRecipeType<*> {
        return IRecipeType.CRAFTING
    }

    override fun getSerializer(): IRecipeSerializer<*> {
        return MBRecipeSerializers.ShulkerBoat
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "shulker_boat")
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width*height >= 2
    }

    override fun getRecipeOutput() = ItemStack(ShulkerBoatItem[BoatType.OAK])

    private fun getBoatType(item: Item): BoatType? {
        return BoatType.values().firstOrNull { it.provideBoatItem() == item }
    }

    override fun getCraftingResult(inv: CraftingInventory): ItemStack {
        var boatType: BoatType? = null
        var shulkerBox: ItemStack? = null
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            val correspondingBoatType = getBoatType(stack.item)
            if(correspondingBoatType != null) {
                if(boatType != null) {
                    return ItemStack.EMPTY
                }
                boatType = correspondingBoatType
            } else if(stack.item is BlockItem && (stack.item as BlockItem).block is ShulkerBoxBlock) {
                if(shulkerBox != null) {
                    return ItemStack.EMPTY
                }
                shulkerBox = stack
            } else if(!stack.isEmpty) {
                return ItemStack.EMPTY
            }
        }
        if(shulkerBox != null && boatType != null) {
            val stack = ItemStack(ShulkerBoatItem[boatType])
            stack.getOrCreateTagElement("AdditionalData").put("TileEntityData", shulkerBox.getOrCreateTagElement("BlockEntityTag"))
            val color = ShulkerBoxBlock.getColorFromItem(shulkerBox.item)
            if(color != null) {
                stack.getOrCreateTagElement("AdditionalData").putString("Color", color.getName())
            }
            return stack
        }
        return ItemStack.EMPTY
    }

    override fun matches(inv: CraftingInventory, worldIn: World?): Boolean {
        return getCraftingResult(inv) != ItemStack.EMPTY
    }
}
