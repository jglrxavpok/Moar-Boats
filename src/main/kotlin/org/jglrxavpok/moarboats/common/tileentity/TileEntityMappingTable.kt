package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.InventoryHelper
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.NonNullList

class TileEntityMappingTable: TileEntity() {

    val inventory = InventoryBasic("mapping_table", false, 1)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val invList = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
        for(i in 0 until inventory.sizeInventory) {
            invList += inventory.getStackInSlot(i)
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
}