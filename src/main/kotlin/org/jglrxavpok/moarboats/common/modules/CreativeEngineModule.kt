package org.jglrxavpok.moarboats.common.modules

import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer

object CreativeEngineModule : BaseEngineModule() {
    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? {
        return EmptyModuleContainer(containerID, player.inventory, boat, isLarge = true)
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

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        return true
    }

    override fun updateFuelState(boat: IControllable, state: CompoundTag, inv: Container) {
        // NOP
    }

    override fun getFuelTime(fuelItem: ItemStack): Int {
        return 0
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
    }

}