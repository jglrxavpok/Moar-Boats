package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
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

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        val heldItem = player.getItemInHand(hand)
        val lazyCapa = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
        if(lazyCapa.isPresent) {
            lazyCapa.ifPresent { capability ->
                val potentialDrain = capability.drain(1000, false)
                if(potentialDrain != null) {
                    val accepted = this.fill(from, potentialDrain, true)
                    if(accepted > 0) {
                        val drained = capability.drain(1000, true)!!
                        player.setItemInHand(hand, capability.container)
                        this.fill(from, drained, false)
                    }
                    return true
                }
            }
        }
        return false
    }

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) {
        fluidNameProperty[to] = ""
        fluidAmountProperty[to] = 0
    }

    override fun createContainer(player: PlayerEntity, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory)
    }

    override fun createGui(player: PlayerEntity, boat: IControllable): Screen {
        return GuiTankModule(player.inventory, this, boat)
    }

    override fun getCapacity(boat: IControllable): Int {
        return MoarBoatsConfig.fluidTank.tankCapacity.get()
    }

    override fun canBeFilled(boat: IControllable) = true

    override fun canBeDrained(boat: IControllable) = true

    override fun canBeFilled(boat: IControllable, fluidStack: FluidStack) = true

    override fun canBeDrained(boat: IControllable, fluidStack: FluidStack) = true

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(BlockBoatTank.asItem(), 1)
    }

}