package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IBoatModuleInventory
import org.jglrxavpok.moarboats.modules.IControllable

object EngineTest: BoatModule() {

    override val id = ResourceLocation("moarboats:testEngine")
    override val usesInventory = true
    override val moduleType = Type.Engine

    const val SECONDS_TO_TICKS = 20

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        state.setInteger("fuelTotalTime", 0)
        state.setInteger("fuelTime", 0)
        to.saveState()
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) {
        if(!player.world.isRemote) {
            player.openGui(MoarBoats, MoarBoatsGuiHandler.EngineGui, player.world, from.entityID, 0, 0)
        }
    }

    override fun controlBoat(from: IControllable) {
        if(hasFuel(from)) {
            from.accelerate((1.0+rng.nextFloat()).toFloat())
            from.turnRight()
        }
    }

    fun hasFuel(from: IControllable): Boolean {
        val state = from.getState()
        val fuelTime = state.getInteger("fuelTime")
        val fuelTotalTime = state.getInteger("fuelTotalTime")
        return fuelTime < fuelTotalTime
    }

    override fun update(from: IControllable) {
        val state = from.getState()
        val inv = from.getInventory()
        updateFuelState(from, state, inv)
    }

    private fun updateFuelState(boat: IControllable, state: NBTTagCompound, inv: IInventory) {
        val fuelTime = state.getInteger("fuelTime")
        val fuelTotalTime = state.getInteger("fuelTotalTime")
        if(fuelTime < fuelTotalTime) {
            state.setInteger("fuelTime", fuelTime+1)
        } else {
            val stack = inv.getStackInSlot(0)
            val fuelItem = stack.item
            val itemFuelTime = getFuelTime(fuelItem)
            if (itemFuelTime > 0) {
                if(fuelItem == Items.LAVA_BUCKET)
                    inv.setInventorySlotContents(0, ItemStack(Items.BUCKET))
                else
                    inv.decrStackSize(0, 1)
            }
            state.setInteger("fuelTime", 0)
            state.setInteger("fuelTotalTime", itemFuelTime)
        }
        boat.saveState()
    }

    private fun getFuelTime(fuelItem: Item): Int {
        return when(fuelItem) {
            Items.COAL -> 60*3*SECONDS_TO_TICKS
            Items.LAVA_BUCKET -> 60*15*SECONDS_TO_TICKS
            Item.getItemFromBlock(Blocks.MAGMA) -> 60*30*SECONDS_TO_TICKS
            else -> 0
        }
    }
}

class EngineModuleInventory(val inventoryName: String, override val boat: IControllable, override val module: BoatModule): InventoryBasic("testEngine",  true,1), IBoatModuleInventory {

    override val list = NonNullList.withSize(1, ItemStack.EMPTY)

    override fun getField(id: Int): Int {
        val key = id2key(id)
        if(key != null)
            return getModuleState().getInteger(key)
        return -1
    }

    private fun id2key(id: Int): String? = when(id) {
        0 -> "fuelTime"
        1 -> "fuelTotalTime"
        else -> null
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun markDirty() {
    }

    override fun getStackInSlot(index: Int): ItemStack {
        return list[index]
    }

    override fun decrStackSize(index: Int, count: Int) = ItemStackHelper.getAndSplit(list, index, count)

    override fun clear() {
        list.clear()
    }

    override fun getSizeInventory() = list.size

    override fun getName() = inventoryName

    override fun isEmpty(): Boolean {
        return list.all { it.isEmpty }
    }

    override fun getDisplayName(): ITextComponent {
        return TextComponentTranslation("inventory.$inventoryName.name")
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        if(index == 0) {
            val itemstack = list[0]
            return TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && itemstack.item !== Items.BUCKET
        }
        return false
    }

    override fun getInventoryStackLimit() = 64

    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return true
    }

    override fun openInventory(player: EntityPlayer?) {

    }

    override fun setField(id: Int, value: Int) {
        val key = id2key(id)
        if(key != null) {
            getModuleState().setInteger(key, value)
            saveModuleState()
        }
    }

    override fun closeInventory(player: EntityPlayer?) {

    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        list[index] = stack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        return ItemStackHelper.getAndRemove(list, index)
    }

    override fun getFieldCount(): Int {
        return 2
    }

}