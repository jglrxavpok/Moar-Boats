package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiTankModule
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.BlockBoatTank
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import org.jglrxavpok.moarboats.common.state.StringBoatProperty

object FluidTankModule: BoatModule(), IFluidBoatModule {
    override val id = ResourceLocation(MoarBoats.ModID, "fluid_tank")
    override val usesInventory = false
    override val moduleSpot = Spot.Storage

    override val fluidNameProperty = StringBoatProperty("fluid_name")
    override val fluidAmountProperty = IntBoatProperty("fluid_amount")

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        val heldItem = player.getHeldItem(hand)
        if(heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val capability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
            capability?.let {
                val potentialDrain = capability.drain(1000, false)
                if(potentialDrain != null) {
                    val accepted = this.fill(from, potentialDrain, true)
                    if(accepted > 0) {
                        val drained = capability.drain(1000, true)!!
                        player.setHeldItem(hand, capability.container)
                        this.fill(from, drained, false)
                    }
                    return true
                }
            }
            return false
        }
        return false
    }

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) {
        fluidNameProperty[to] = ""
        fluidAmountProperty[to] = 0
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory)
    }

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiTankModule(player.inventory, this, boat)
    }

    override fun getCapacity(boat: IControllable): Int {
        return MoarBoatsConfig.fluidTank.tankCapacity
    }

    override fun canBeFilled(boat: IControllable) = true

    override fun canBeDrained(boat: IControllable) = true

    override fun canBeFilled(boat: IControllable, fluidStack: FluidStack) = true

    override fun canBeDrained(boat: IControllable, fluidStack: FluidStack) = true

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(ItemBlock.getItemFromBlock(BlockBoatTank), 1)
    }

}