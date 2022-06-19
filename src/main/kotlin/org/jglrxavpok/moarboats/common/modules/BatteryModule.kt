package org.jglrxavpok.moarboats.common.modules

import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiBatteryModule
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.BlockBoatBattery
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.state.IntBoatProperty

object BatteryModule: BoatModule(), IEnergyBoatModule {
    override val energyProperty = IntBoatProperty("energy")

    override fun canReceiveEnergy(boat: IControllable) = true
    override fun canGiveEnergy(boat: IControllable) = true

    override fun getMaxStorableEnergy(boat: IControllable): Int {
        return MoarBoatsConfig.boatBattery.maxEnergy.get()
    }

    override val id: ResourceLocation = ResourceLocation(MoarBoats.ModID, "battery")
    override val usesInventory = false
    override val moduleSpot = Spot.Storage

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) {
        energyProperty[to] = 0
    }

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? = EmptyModuleContainer(containerID, player.inventory, boat)
    override fun getMenuType() = ContainerTypes.EmptyModuleMenu.get()

    override fun createGui(containerID: Int, player: Player, boat: IControllable) = GuiBatteryModule(containerID, player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(MBItems.BoatBattery.get(), 1)
    }
}