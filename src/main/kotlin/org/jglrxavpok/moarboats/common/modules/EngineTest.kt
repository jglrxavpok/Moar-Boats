package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiTestEngine
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.containers.ContainerTestEngine
import org.jglrxavpok.moarboats.common.modules.inventories.BaseModuleInventory
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IBoatModuleInventory
import org.jglrxavpok.moarboats.modules.IControllable

object EngineTest: BoatModule() {

    @SideOnly(Side.CLIENT)
    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiTestEngine(player.inventory, this, boat)
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container {
        return ContainerTestEngine(player.inventory, this, boat)
    }

    override val id = ResourceLocation("moarboats:furnace_engine")
    override val usesInventory = true
    override val moduleType = Type.Engine

    const val SECONDS_TO_TICKS = 20

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        state.setInteger("fuelTotalTime", 0)
        state.setInteger("fuelTime", 0)
        to.saveState()
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
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
                println("using fuel!")
                if(fuelItem == Items.LAVA_BUCKET)
                    inv.setInventorySlotContents(0, ItemStack(Items.BUCKET))
                else
                    inv.decrStackSize(0, 1)
            }
            state.setInteger("fuelTime", 0)
            state.setInteger("fuelTotalTime", itemFuelTime)
        }

        if(hasFuel(boat) && rng.nextInt(4) == 0) {
            boat.worldRef.spawnParticle(EnumParticleTypes.SMOKE_LARGE, boat.positionX, boat.positionY + 0.8, boat.positionZ, 0.0, 0.0, 0.0)
        }
        boat.saveState()
    }

    private fun getFuelTime(fuelItem: Item): Int {
        return when(fuelItem) {
            Items.COAL -> 20*5// FIXME 60*3*SECONDS_TO_TICKS
            Items.LAVA_BUCKET -> 60*15*SECONDS_TO_TICKS
            Item.getItemFromBlock(Blocks.MAGMA) -> 60*30*SECONDS_TO_TICKS
            else -> 0
        }
    }
}