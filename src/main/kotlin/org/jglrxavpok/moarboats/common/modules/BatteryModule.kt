package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiBatteryModule
import org.jglrxavpok.moarboats.common.NewConfig
import org.jglrxavpok.moarboats.common.blocks.BlockBoatBattery
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.state.IntBoatProperty

object BatteryModule: BoatModule(), IEnergyBoatModule {
    override val energyProperty = IntBoatProperty("energy")

    override fun canReceiveEnergy(boat: IControllable) = true
    override fun canGiveEnergy(boat: IControllable) = true

    override fun getMaxStorableEnergy(boat: IControllable): Int {
        return NewConfig.boatBattery.maxEnergy
    }

    override val id: ResourceLocation = ResourceLocation(MoarBoats.ModID, "battery")
    override val usesInventory = false
    override val moduleSpot = Spot.Storage

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) {
        energyProperty[to] = 0
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiBatteryModule(player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(ItemBlock.getItemFromBlock(BlockBoatBattery), 1)
    }
}