package org.jglrxavpok.moarboats.common.modules

import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.items.OarsItem

object OarEngineModule: BaseEngineModule(), BlockReason {

    override val id = ResourceLocation("moarboats:oar_engine")

    override val usesInventory = false

    private fun isOccupied(boat: IControllable): Boolean {
        return boat.correspondingEntity.controllingPassenger != null
    }

    override fun controlBoat(from: IControllable) {
        val controllingEntity = from.correspondingEntity.controllingPassenger as? Player ?: return
        val forward = controllingEntity.zza
        val strafe = controllingEntity.xxa

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

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        return isOccupied(from)
    }

    override fun updateFuelState(boat: IControllable, state: CompoundTag, inv: Container) {
        // NOP
    }

    override fun getFuelTime(fuelItem: ItemStack): Int {
        return 0
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(MBItems.OarsItem.get(), 1)
    }
}