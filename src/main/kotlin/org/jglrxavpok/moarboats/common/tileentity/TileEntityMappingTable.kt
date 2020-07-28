package org.jglrxavpok.moarboats.common.tileentity

import net.minecraft.block.BlockState
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

    override fun write(compound: CompoundNBT): CompoundNBT {
        val invList = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
        for(i in 0 until inventory.sizeInventory) {
            invList[i] = inventory.getStackInSlot(i) ?: ItemStack.EMPTY
        }
        ItemStackHelper.saveAllItems(compound, invList)
        return super.write(compound)
    }

    override fun deserializeNBT(state: BlockState, compound: CompoundNBT) {
        super.deserializeNBT(state, compound)
        val invList = NonNullList.withSize(inventory.sizeInventory, ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(compound, invList)
        inventory.clear()
        for(i in 0 until inventory.sizeInventory) {
            inventory.setInventorySlotContents(i, invList.get(i))
        }
    }

    override fun <T> getCapability(capability: net.minecraftforge.common.capabilities.Capability<T>, facing: net.minecraft.util.Direction?): LazyOptional<T> {
        if (capability === net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of { invWrapper }.cast()
        }
        return super.getCapability(capability, facing)
    }
}