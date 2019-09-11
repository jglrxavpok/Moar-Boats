package org.jglrxavpok.moarboats.common.items

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats

object GoldenTicketCopyRecipe: IRecipe {
    override fun getSerializer(): IRecipeSerializer<*> {
        return MBRecipeSerializers.CopyGoldenTicket
    }

    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "golden_itinerary")
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width*height >= 2
    }

    override fun getRecipeOutput() = ItemStack.EMPTY

    override fun getCraftingResult(inv: IInventory): ItemStack {
        var emptyTickets = 0
        var fullTickets = 0
        var fullTicket: ItemStack? = null
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(stack.item == ItemGoldenTicket) {
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

    override fun matches(inv: IInventory, worldIn: World?): Boolean {
        var emptyTickets = 0
        var fullTickets = 0
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(stack.item == ItemGoldenTicket) {
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
