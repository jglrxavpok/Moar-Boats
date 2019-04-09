package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.InventoryHelper
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.InvWrapper

class TileEntityMappingTable: TileEntity() {

    val inventory = InventoryBasic("mapping_table", false, 1)
    val invWrapper = InvWrapper(inventory)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val invList = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
        for(i in 0 until inventory.sizeInventory) {
            invList[i] = inventory.getStackInSlot(i) ?: ItemStack.EMPTY
        }
        ItemStackHelper.saveAllItems(compound, invList)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        val invList = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(compound, invList)
        inventory.clear()
        for(i in 0 until inventory.sizeInventory) {
            inventory.setInventorySlotContents(i, invList.get(i))
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true
        }
        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: net.minecraftforge.common.capabilities.Capability<T>, facing: net.minecraft.util.EnumFacing?): T? {
        if (capability === net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return invWrapper as T
        }
        return super.getCapability(capability, facing)
    }
}