package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.EnumLightType
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import kotlin.math.roundToInt

object SolarEngineModule : BaseEngineModule() {

    val invertedProperty = BooleanBoatProperty("inverted")

    override fun createContainer(player: PlayerEntity, boat: IControllable): ContainerBase {
        return EmptyContainer(player.inventory, isLarge = true)
    }

    override val id = ResourceLocation("moarboats:solar_engine")

    override val usesInventory = false

    override fun estimatedTotalTicks(boat: IControllable): Float {
        return Float.NaN
    }

    override fun remainingTimeInTicks(from: IControllable): Float {
        return Float.NaN
    }

    override fun remainingTimeInPercent(from: IControllable): Float {
        val levelIn = from.worldRef
        val pos = from.correspondingEntity.position
        var diff = levelIn.getLightFor(EnumLightType.SKY, pos) - levelIn.skylightSubtracted
        var angle = levelIn.getCelestialAngleRadians(1.0f)

        if (invertedProperty[from]) {
            diff = 15 - diff
        }

        if (diff > 0 && !invertedProperty[from]) {
            val f1 = if (angle < Math.PI.toFloat()) 0.0f else Math.PI.toFloat() * 2f
            angle += (f1 - angle) * 0.2f
            diff = (diff.toFloat() * MathHelper.cos(angle)).roundToInt()
        }

        return MathHelper.clamp(diff, 0, 15) / 15f
    }

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        if(sneaking && player.getItemInHand(hand).isEmpty) {
            invertedProperty[from] = !invertedProperty[from]
            return true
        }
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        return remainingTimeInPercent(from) >= 1f/15f - 10e-14f
    }

    override fun updateFuelState(boat: IControllable, state: CompoundNBT, inv: IInventory) {
        // NOP
    }

    override fun getFuelTime(fuelItem: ItemStack): Int {
        return 0
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(Blocks.DAYLIGHT_DETECTOR.asItem(), 1)
    }

}