package org.jglrxavpok.moarboats.common.items

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import java.util.*

object UpgradeToGoldenTicketRecipe: IRecipe {
    override fun getId(): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "upgrade_to_golden_itinerary")
    }

    override fun getSerializer(): IRecipeSerializer<*> {
        return MBRecipeSerializers.UpgradeToGoldenTicket
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width*height >= 2
    }

    override fun getRecipeOutput() = ItemStack.EMPTY

    override fun getCraftingResult(inv: IInventory): ItemStack {
        var emptyTickets = 0
        var fullMaps = 0
        var fullMap: ItemStack? = null
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(stack.item == ItemGoldenTicket) {
                if(ItemGoldenTicket.isEmpty(stack)) {
                    emptyTickets++
                } else {
                    return ItemStack.EMPTY
                }
            } else if(stack.item == MapItemWithPath) {
                fullMaps++
                fullMap = stack
            } else if(!stack.isEmpty) {
                return ItemStack.EMPTY
            }
        }
        if(fullMaps == 1 && fullMap != null && emptyTickets >= 1) {
            val stack = ItemGoldenTicket.createStack(UUID.randomUUID().toString())
            ItemGoldenTicket.updateItinerary(stack, MapItemWithPath, fullMap)
            return stack
        }
        return ItemStack.EMPTY
    }

    override fun matches(inv: IInventory, levelIn: World?): Boolean {
        var emptyTickets = 0
        var fullMaps = 0
        for(i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if(stack.item == ItemGoldenTicket) {
                if(ItemGoldenTicket.isEmpty(stack)) {
                    emptyTickets++
                } else {
                    return false
                }
            } else if(stack.item == MapItemWithPath) {
                fullMaps++
            } else if(!stack.isEmpty) {
                return false
            }
        }
        return fullMaps == 1 && emptyTickets >= 1
    }
}
