package org.jglrxavpok.moarboats.common.modules

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.Container
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.block.Blocks
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import kotlin.math.roundToInt

object SolarEngineModule : BaseEngineModule() {

    val invertedProperty = BooleanBoatProperty("inverted")

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
        val pos = from.correspondingEntity.blockPosition()
        var diff = levelIn.getBrightness(LightLayer.SKY, pos) - levelIn.skyDarken
        var angle = levelIn.getSunAngle(1.0f)

        if (invertedProperty[from]) {
            diff = 15 - diff
        }

        if (diff > 0 && !invertedProperty[from]) {
            val f1 = if (angle < Math.PI.toFloat()) 0.0f else Math.PI.toFloat() * 2f
            angle += (f1 - angle) * 0.2f
            diff = (diff.toFloat() * Mth.cos(angle)).roundToInt()
        }

        return Mth.clamp(diff, 0, 15) / 15f
    }

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
        if(sneaking && player.getItemInHand(hand).isEmpty) {
            invertedProperty[from] = !invertedProperty[from]
            return true
        }
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        return remainingTimeInPercent(from) >= 1f/15f - 10e-14f
    }

    override fun updateFuelState(boat: IControllable, state: CompoundTag, inv: Container) {
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
