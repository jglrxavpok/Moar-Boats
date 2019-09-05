package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiBatteryModule
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.blocks.BlockBoatBattery
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
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

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) {
        energyProperty[to] = 0
    }

    override fun createContainer(player: PlayerEntity, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: PlayerEntity, boat: IControllable) = GuiBatteryModule(player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(BlockItem.getItemFromBlock(BlockBoatBattery), 1)
    }
}