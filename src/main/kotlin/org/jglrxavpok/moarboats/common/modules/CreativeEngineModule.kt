package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

object CreativeEngineModule : BaseEngineModule() {
    override fun createContainer(player: PlayerEntity, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory, isLarge = true)
    }

    override val id = ResourceLocation("moarboats:creative_engine")

    override val usesInventory = false

    override fun estimatedTotalTicks(boat: IControllable): Float {
        return Float.POSITIVE_INFINITY
    }

    override fun remainingTimeInTicks(from: IControllable): Float {
        return Float.POSITIVE_INFINITY
    }

    override fun remainingTimeInPercent(from: IControllable): Float {
        return Float.POSITIVE_INFINITY
    }

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        return true
    }

    override fun updateFuelState(boat: IControllable, state: CompoundNBT, inv: IInventory) {
        // NOP
    }

    override fun getFuelTime(fuelItem: ItemStack): Int {
        return 0
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
    }

}