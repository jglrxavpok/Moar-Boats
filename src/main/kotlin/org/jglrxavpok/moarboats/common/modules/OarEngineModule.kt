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
import org.jglrxavpok.moarboats.common.items.OarsItem
import org.jglrxavpok.moarboats.common.items.SeatItem

object OarEngineModule: BaseEngineModule(), BlockReason {
    override fun createContainer(player: PlayerEntity, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory, isLarge = true)
    }

    override val id = ResourceLocation("moarboats:oar_engine")

    override val usesInventory = false

    private fun isOccupied(boat: IControllable): Boolean {
        return boat.correspondingEntity.controllingPassenger != null
    }

    override fun controlBoat(from: IControllable) {
        val controllingEntity = from.correspondingEntity.controllingPassenger as? PlayerEntity ?: return
        val forward = controllingEntity.moveForward
        val strafe = controllingEntity.moveStrafing

        val forwardMultiplier = 0.75f
        val strafeMultiplier = 0.75f
        if (forward > 0.001f) {
            from.accelerate(forward*forwardMultiplier)
        }
        if(forward < -0.001f) {
            from.decelerate(-forward)
        }

        from.turnLeft(strafe*strafeMultiplier)
    }

    override fun estimatedTotalTicks(boat: IControllable): Float {
        return if(isOccupied(boat)) {
            Float.POSITIVE_INFINITY
        } else {
            0f;
        }
    }

    override fun remainingTimeInTicks(from: IControllable): Float {
        return if(isOccupied(from)) {
            Float.POSITIVE_INFINITY
        } else {
            0f;
        }
    }

    override fun remainingTimeInPercent(from: IControllable): Float {
        return if(isOccupied(from)) {
            Float.POSITIVE_INFINITY
        } else {
            0f;
        }
    }

    override fun onAddition(to: IControllable) {
        super.onAddition(to)
        stationaryProperty[to] = false
    }

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        return isOccupied(from)
    }

    override fun updateFuelState(boat: IControllable, state: CompoundNBT, inv: IInventory) {
        // NOP
    }

    override fun getFuelTime(fuelItem: ItemStack): Int {
        return 0
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(OarsItem, 1)
    }
}