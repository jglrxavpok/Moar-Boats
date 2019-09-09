package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.inventory.Inventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Direction
import net.minecraft.util.NonNullList
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import org.jglrxavpok.moarboats.MoarBoats

class TileEntityMappingTable: TileEntity(MoarBoats.TileEntityMappingTableType) {

    val inventory = Inventory(1)
    val invWrapper = InvWrapper(inventory)

    override fun save(compound: CompoundNBT): CompoundNBT {
        val invList = NonNullList.withSize(inventory.containerSize, ItemStack.EMPTY)
        for(i in 0 until inventory.containerSize) {
            invList[i] = inventory.getItem(i) ?: ItemStack.EMPTY
        }
        ItemStackHelper.saveAllItems(compound, invList)
        return super.save(compound)
    }

    override fun load(compound: CompoundNBT) {
        super.load(compound)
        val invList = NonNullList.withSize(inventory.containerSize, ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(compound, invList)
        inventory.clearContent()
        for(i in 0 until inventory.containerSize) {
            inventory.setItem(i, invList.get(i))
        }
    }

    override fun <T> getCapability(capability: net.minecraftforge.common.capabilities.Capability<T>, facing: net.minecraft.util.Direction?): LazyOptional<T> {
        if (capability === net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of { invWrapper }.cast()
        }
        return super.getCapability(capability, facing)
    }
}