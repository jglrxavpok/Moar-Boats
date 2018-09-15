package org.jglrxavpok.moarboats.common.items

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.oredict.DyeUtils
import net.minecraftforge.registries.IForgeRegistryEntry
import org.jglrxavpok.moarboats.MoarBoats

object GoldenItineraryCopyRecipe: IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "golden_itinerary")
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width*height >= 2
    }

    override fun getRecipeOutput() = ItemStack.EMPTY

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        var emptyTickets = 0
        var fullTickets = 0
        var fullTicket: ItemStack? = null
        for(i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)
            if(stack.item == ItemGoldenItinerary) {
                if(ItemGoldenItinerary.isEmpty(stack)) {
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
            val stack = ItemGoldenItinerary.createStack(ItemGoldenItinerary.getUUID(fullTicket).toString())
            stack.count = emptyTickets+1
            return stack
        }
        return ItemStack.EMPTY
    }

    override fun matches(inv: InventoryCrafting, worldIn: World?): Boolean {
        var emptyTickets = 0
        var fullTickets = 0
        for(i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)
            if(stack.item == ItemGoldenItinerary) {
                if(ItemGoldenItinerary.isEmpty(stack)) {
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