package org.jglrxavpok.moarboats.common.items

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.ShulkerBoxBlock
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.data.BoatType

object ShulkerBoatRecipe: CraftingRecipe {
    override fun getType(): RecipeType<*> {
        return RecipeType.CRAFTING
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return MBRecipeSerializers.ShulkerBoat.get()
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "shulker_boat")
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width*height >= 2
    }

    override fun getResultItem() = ItemStack(MBItems.ShulkerBoats[BoatType.OAK]!!.get())

    private fun getBoatType(item: Item): BoatType? {
        return BoatType.values().firstOrNull { it.provideBoatItem() == item }
    }

    override fun assemble(inv: CraftingContainer): ItemStack {
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
            val stack = ItemStack(MBItems.ShulkerBoats[BoatType.OAK]!!.get())
            stack.getOrCreateTagElement("AdditionalData").put("TileEntityData", shulkerBox.getOrCreateTagElement("BlockEntityTag"))
            val color = ShulkerBoxBlock.getColorFromItem(shulkerBox.item)
            if(color != null) {
                stack.getOrCreateTagElement("AdditionalData").putString("Color", color.getName())
            }
            return stack
        }
        return ItemStack.EMPTY
    }

    override fun matches(inv: CraftingContainer, worldIn: Level?): Boolean {
        return assemble(inv) != ItemStack.EMPTY
    }
}
