package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.*
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.client.gui.GuiFurnaceEngine
import org.jglrxavpok.moarboats.common.containers.ContainerFurnaceEngine
import org.jglrxavpok.moarboats.extensions.toRadians
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

object FurnaceEngineModule : BoatModule() {

    @SideOnly(Side.CLIENT)
    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiFurnaceEngine(player.inventory, this, boat)
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container {
        return ContainerFurnaceEngine(player.inventory, this, boat)
    }

    override val id = ResourceLocation("moarboats:furnace_engine")
    override val usesInventory = true
    override val moduleType = Type.Engine

    const val SECONDS_TO_TICKS = 20

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        state.setInteger("fuelTotalTime", 0)
        state.setInteger("fuelTime", 0)
        state.setBoolean("stationary", false)
        to.saveState()
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        if(hasFuel(from) && !isStationary(from)) {
            from.accelerate()
        }
    }

    fun isStationary(from: IControllable) = from.getState().getBoolean("stationary")

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
            if (itemFuelTime > 0 && !isStationary(boat)) { // don't consume a new item if you are not moving
                if(fuelItem == Items.LAVA_BUCKET)
                    inv.setInventorySlotContents(0, ItemStack(Items.BUCKET))
                else
                    inv.decrStackSize(0, 1)
                state.setInteger("fuelTime", 0)
                state.setInteger("fuelTotalTime", itemFuelTime)
            }
        }

        if(hasFuel(boat) && rng.nextInt(4) == 0) {
            val cos = MathHelper.cos((boat.yaw + 90f).toRadians())
            val sin = MathHelper.sin((boat.yaw + 90f).toRadians())
            val dist = 0.5
            boat.worldRef.spawnParticle(EnumParticleTypes.SMOKE_LARGE, boat.positionX + dist * cos, boat.positionY + 0.8, boat.positionZ + dist * sin, 0.0, 0.0, 0.0)
        }
        boat.saveState()
    }

    fun getFuelTime(fuelItem: Item): Int {
        return when(fuelItem) {
            Item.getItemFromBlock(Blocks.TORCH) -> 1*SECONDS_TO_TICKS
            Items.COAL -> 60*3*SECONDS_TO_TICKS
            Items.LAVA_BUCKET -> 60*15*SECONDS_TO_TICKS
            Item.getItemFromBlock(Blocks.MAGMA) -> 60*30*SECONDS_TO_TICKS
            Item.getItemFromBlock(Blocks.COAL_BLOCK) -> 30*30*SECONDS_TO_TICKS
            else -> 0
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(ItemBlock.getItemFromBlock(Blocks.FURNACE), 1)
    }

    fun changeStationaryState(boat: IControllable) {
        val state = boat.getState()
        val isStationary = state.getBoolean("stationary")
        state.setBoolean("stationary", !isStationary)
        boat.saveState()
    }

    fun isItemFuel(fuelItem: ItemStack) = getFuelTime(fuelItem.item) > 0
}