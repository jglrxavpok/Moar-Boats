package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.ContainerFurnaceEngine
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import org.jglrxavpok.moarboats.extensions.toRadians

object FurnaceEngineModule : BaseEngineModule() {
    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase {
        return ContainerFurnaceEngine(player.inventory, this, boat)
    }

    override val id = ResourceLocation("moarboats:furnace_engine")
    override val usesInventory = true

    // State names
    val fuelTotalTimeProperty = IntBoatProperty("fuelTotalTime")
    val fuelTimeProperty = IntBoatProperty("fuelTime")

    override fun remainingTimeInTicks(from: IControllable): Float {
        return (fuelTotalTimeProperty[from] - fuelTimeProperty[from]).toFloat()
    }

    override fun remainingTimeInPercent(from: IControllable): Float {
        val currentFuel = fuelTimeProperty[from]
        val totalFuel = fuelTotalTimeProperty[from]
        return if(totalFuel == 0) 0f else 1f - currentFuel / totalFuel.toFloat()
    }

    override fun estimatedTotalTicks(boat: IControllable): Float {
        val inv = boat.getInventory()
        val diff = remainingTimeInTicks(boat)
        val currentStack = inv.getStackInSlot(0)
        return diff + currentStack.count * getFuelTime(currentStack.item)
    }

    override fun onAddition(to: IControllable) {
        super.onAddition(to)
        fuelTimeProperty[to] = 0
        fuelTotalTimeProperty[to] = 0
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        val fuelTime = fuelTimeProperty[from]
        val fuelTotalTime = fuelTotalTimeProperty[from]
        return fuelTime < fuelTotalTime
    }

    override fun updateFuelState(boat: IControllable, state: NBTTagCompound, inv: IInventory) {
        val fuelTime = fuelTimeProperty[boat]
        val fuelTotalTime = fuelTotalTimeProperty[boat]
        if(fuelTime < fuelTotalTime) {
            fuelTimeProperty[boat]++
        } else {
            val stack = inv.getStackInSlot(0)
            val fuelItem = stack.item
            val itemFuelTime = getFuelTime(fuelItem)
            if (itemFuelTime > 0 && !isStationary(boat)) { // don't consume a new item if you are not moving
                if(fuelItem == Items.LAVA_BUCKET)
                    inv.setInventorySlotContents(0, ItemStack(Items.BUCKET))
                else
                    inv.decrStackSize(0, 1)
                fuelTimeProperty[boat] = 0
                fuelTotalTimeProperty[boat] = itemFuelTime
            }
        }

        if(hasFuel(boat) && rng.nextInt(4) == 0) {
            val cos = MathHelper.cos((boat.yaw + 90f).toRadians())
            val sin = MathHelper.sin((boat.yaw + 90f).toRadians())
            val dist = 0.5
            boat.worldRef.spawnParticle(EnumParticleTypes.SMOKE_LARGE, boat.positionX + dist * cos, boat.positionY + 0.8, boat.positionZ + dist * sin, 0.0, 0.0, 0.0)
        }
    }

    override fun getFuelTime(fuelItem: Item): Int {
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
}