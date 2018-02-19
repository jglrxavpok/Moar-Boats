package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.Block
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
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.client.gui.GuiFurnaceEngine
import org.jglrxavpok.moarboats.common.containers.ContainerFurnaceEngine
import org.jglrxavpok.moarboats.extensions.toRadians
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.modules.FurnaceEngineModule.saveState

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
    override val moduleSpot = Spot.Engine
    override val hopperPriority = 10

    const val SECONDS_TO_TICKS = 20

    // State names
    const val STATIONARY = "stationary"
    const val FUEL_TOTAL_TIME = "fuelTotalTime"
    const val FUEL_TIME = "fuelTime"
    const val LOCKED_BY_REDSTONE = "redstoneLocked"

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        state.setInteger(FUEL_TOTAL_TIME, 0)
        state.setInteger(FUEL_TIME, 0)
        state.setBoolean(STATIONARY, false)
        state.setBoolean(LOCKED_BY_REDSTONE, false)
        to.saveState()
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        if(hasFuel(from) && !isStationary(from) && from.inWater()) {
            from.accelerate()
        }
    }

    fun isStationary(from: IControllable) = from.getState().getBoolean(STATIONARY) || from.getState().getBoolean(LOCKED_BY_REDSTONE)

    fun hasFuel(from: IControllable): Boolean {
        val state = from.getState()
        val fuelTime = state.getInteger(FUEL_TIME)
        val fuelTotalTime = state.getInteger(FUEL_TOTAL_TIME)
        return fuelTime < fuelTotalTime
    }

    override fun update(from: IControllable) {
        val state = from.getState()
        val inv = from.getInventory()
        updateFuelState(from, state, inv)
        val world = from.worldRef
        state.setBoolean(LOCKED_BY_REDSTONE, world.isBlockPowered(from.correspondingEntity.position))
        from.saveState()
        if(hasFuel(from) && !isStationary(from)) {
            val count = ((Math.random() * 5) + 3).toInt()
            val posX = from.positionX
            val posY = from.positionY
            val posZ = from.positionZ
            val rotationYaw = from.yaw
            val angle = (rotationYaw + 90f).toRadians()
            val distAlongLength = 0.0625f * 17f * -1f

            val pos = BlockPos.PooledMutableBlockPos.retain(posX, posY-0.5, posZ)
            val blockState = from.worldRef.getBlockState(pos)

            repeat(count) {
                // between -8s and 8s, s being the scaling factor
                val distAlongWidth = 0.0625f * 8f * (Math.random() * 2.0 - 1.0)
                val anchorX = posX + MathHelper.cos(angle) * distAlongLength + MathHelper.sin(angle) * distAlongWidth
                val anchorY = posY + 0.0625f * 4f
                val anchorZ = posZ + MathHelper.sin(angle) * distAlongLength - MathHelper.cos(angle) * distAlongWidth
                from.worldRef.spawnParticle(EnumParticleTypes.BLOCK_CRACK, anchorX, anchorY, anchorZ, -from.velocityX, 1.0, -from.velocityZ, Block.getStateId(blockState))
            }
            pos.release()
        }
    }

    private fun updateFuelState(boat: IControllable, state: NBTTagCompound, inv: IInventory) {
        val fuelTime = state.getInteger(FUEL_TIME)
        val fuelTotalTime = state.getInteger(FUEL_TOTAL_TIME)
        if(fuelTime < fuelTotalTime) {
            state.setInteger(FUEL_TIME, fuelTime+1)
        } else {
            val stack = inv.getStackInSlot(0)
            val fuelItem = stack.item
            val itemFuelTime = getFuelTime(fuelItem)
            if (itemFuelTime > 0 && !isStationary(boat)) { // don't consume a new item if you are not moving
                if(fuelItem == Items.LAVA_BUCKET)
                    inv.setInventorySlotContents(0, ItemStack(Items.BUCKET))
                else
                    inv.decrStackSize(0, 1)
                state.setInteger(FUEL_TIME, 0)
                state.setInteger(FUEL_TOTAL_TIME, itemFuelTime)
            }
        }

        if(hasFuel(boat) && rng.nextInt(4) == 0) {
            val cos = MathHelper.cos((boat.yaw + 90f).toRadians())
            val sin = MathHelper.sin((boat.yaw + 90f).toRadians())
            val dist = 0.5
            boat.worldRef.spawnParticle(EnumParticleTypes.SMOKE_LARGE, boat.positionX + dist * cos, boat.positionY + 0.8, boat.positionZ + dist * sin, 0.0, 0.0, 0.0)
        }
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
        val isStationary = state.getBoolean(STATIONARY)
        state.setBoolean(STATIONARY, !isStationary)
        boat.saveState()
    }

    fun isItemFuel(fuelItem: ItemStack) = getFuelTime(fuelItem.item) > 0

    fun isLockedByRedstone(boat: IControllable) = boat.getState().getBoolean(LOCKED_BY_REDSTONE)
}