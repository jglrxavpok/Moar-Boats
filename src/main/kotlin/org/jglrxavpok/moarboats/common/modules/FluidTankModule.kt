package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiTankModule
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.BlockBoatTank
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import org.jglrxavpok.moarboats.common.state.StringBoatProperty

object FluidTankModule: BoatModule(), IFluidBoatModule {
    override val id = ResourceLocation(MoarBoats.ModID, "fluid_tank")
    override val usesInventory = false
    override val moduleSpot = Spot.Storage

    override val fluidNameProperty = StringBoatProperty("fluid_name")
    override val fluidAmountProperty = IntBoatProperty("fluid_amount")

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
        val heldItem = player.getItemInHand(hand)
        val lazyCapa = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
        if(lazyCapa.isPresent) {
            return lazyCapa.map { capability ->
                val potentialDrain = capability.drain(1000, IFluidHandler.FluidAction.SIMULATE)
                if(!potentialDrain.isEmpty) {
                    val accepted = this.fill(from, potentialDrain, IFluidHandler.FluidAction.SIMULATE)
                    if(accepted > 0) {
                        val drained = capability.drain(1000, IFluidHandler.FluidAction.EXECUTE)!!
                        player.setItemInHand(hand, capability.container)
                        this.fill(from, drained, IFluidHandler.FluidAction.EXECUTE)
                    }
                    true
                } else {
                    false
                }
            }.orElse(false)
        }
        return false
    }

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) {
        fluidNameProperty[to] = ""
        fluidAmountProperty[to] = 0
    }

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? {
        return EmptyModuleContainer(containerID, player.inventory, boat)
    }

    override fun getMenuType() = ContainerTypes.EmptyModuleMenu.get()

    override fun createGui(containerID: Int, player: Player, boat: IControllable): Screen {
        return GuiTankModule(containerID, player.inventory, this, boat)
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
            boat.correspondingEntity.spawnAtLocation(MBItems.BoatTank.get(), 1)
    }

}