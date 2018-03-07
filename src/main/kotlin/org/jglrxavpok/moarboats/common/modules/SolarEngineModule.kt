package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.EnumSkyBlock
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty

object SolarEngineModule : BaseEngineModule() {

    val invertedProperty = BooleanBoatProperty("inverted")

    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase {
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
        val worldIn = from.worldRef
        val pos = from.correspondingEntity.position
        var diff = worldIn.getLightFor(EnumSkyBlock.SKY, pos) - worldIn.skylightSubtracted
        var angle = worldIn.getCelestialAngleRadians(1.0f)

        if (invertedProperty[from]) {
            diff = 15 - diff
        }

        if (diff > 0 && !invertedProperty[from]) {
            val f1 = if (angle < Math.PI.toFloat()) 0.0f else Math.PI.toFloat() * 2f
            angle += (f1 - angle) * 0.2f
            diff = Math.round(diff.toFloat() * MathHelper.cos(angle))
        }

        return MathHelper.clamp(diff, 0, 15) / 15f
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        if(sneaking && player.getHeldItem(hand).isEmpty) {
            invertedProperty[from] = !invertedProperty[from]
            return true
        }
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        return remainingTimeInPercent(from) >= 1f/15f - 10e-14f
    }

    override fun updateFuelState(boat: IControllable, state: NBTTagCompound, inv: IInventory) {
        // NOP
    }

    override fun getFuelTime(fuelItem: Item): Int {
        return 0
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(ItemBlock.getItemFromBlock(Blocks.DAYLIGHT_DETECTOR), 1)
    }

}