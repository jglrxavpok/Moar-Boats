package org.jglrxavpok.moarboats.common.items

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import org.jglrxavpok.moarboats.MoarBoats

object GoldenTicketCopyRecipe: CraftingRecipe {
    override fun getType(): RecipeType<*> {
        return RecipeType.CRAFTING
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return MBRecipeSerializers.CopyGoldenTicket.get()
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "golden_itinerary")
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width*height >= 2
    }

    override fun getResultItem() = ItemStack.EMPTY

    override fun assemble(inv: CraftingContainer): ItemStack {
        var emptyTickets = 0
        var fullTickets = 0
        var fullTicket: ItemStack? = null
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(stack.item is ItemGoldenTicket) {
                if(ItemGoldenTicket.isEmpty(stack)) {
                    emptyTickets++
                } else {
                    fullTickets++
                    fullTicket = stack
                }
            } else if(!stack.isEmpty) {
                return ItemStack.EMPTY
            }
        }
        if(fullTickets == 1 && fullTicket != null && emptyTickets >= 1) {
            val stack = ItemGoldenTicket.createStack(ItemGoldenTicket.getUUID(fullTicket).toString())
            stack.count = emptyTickets+1
            return stack
        }
        return ItemStack.EMPTY
    }

    override fun matches(inv: CraftingContainer, worldIn: Level?): Boolean {
        var emptyTickets = 0
        var fullTickets = 0
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(stack.item is ItemGoldenTicket) {
                if(ItemGoldenTicket.isEmpty(stack)) {
                    emptyTickets++
                } else {
                    fullTickets++
                }
            } else if(!stack.isEmpty) {
                return false
            }
        }
        return fullTickets == 1 && emptyTickets >= 1
    }
}
