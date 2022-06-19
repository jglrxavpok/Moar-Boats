package org.jglrxavpok.moarboats.common.items

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MBItems
import java.util.*

object UpgradeToGoldenTicketRecipe: CraftingRecipe {
    override fun getType(): RecipeType<*> {
        return RecipeType.CRAFTING
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "upgrade_to_golden_itinerary")
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return MBRecipeSerializers.UpgradeToGoldenTicket.get()
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width*height >= 2
    }

    override fun getResultItem() = ItemStack.EMPTY

    override fun assemble(inv: CraftingContainer): ItemStack {
        var emptyTickets = 0
        var fullMaps = 0
        var fullMap: ItemStack? = null
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(stack.item is ItemGoldenTicket) {
                if(ItemGoldenTicket.isEmpty(stack)) {
                    emptyTickets++
                } else {
                    return ItemStack.EMPTY
                }
            } else if(stack.item is MapItemWithPath) {
                fullMaps++
                fullMap = stack
            } else if(!stack.isEmpty) {
                return ItemStack.EMPTY
            }
        }
        if(fullMaps == 1 && fullMap != null && emptyTickets >= 1) {
            val stack = ItemGoldenTicket.createStack(UUID.randomUUID().toString())
            ItemGoldenTicket.updateItinerary(stack, MBItems.MapItemWithPath.get(), fullMap)
            return stack
        }
        return ItemStack.EMPTY
    }

    override fun matches(inv: CraftingContainer, levelIn: Level?): Boolean {
        var emptyTickets = 0
        var fullMaps = 0
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(stack.item is ItemGoldenTicket) {
                if(ItemGoldenTicket.isEmpty(stack)) {
                    emptyTickets++
                } else {
                    return false
                }
            } else if(stack.item is MapItemWithPath) {
                fullMaps++
            } else if(!stack.isEmpty) {
                return false
            }
        }
        return fullMaps == 1 && emptyTickets >= 1
    }
}
