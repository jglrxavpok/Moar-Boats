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
import org.jglrxavpok.moarboats.common.items.CreativeEngineItem
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty

object CreativeEngineModule : BaseEngineModule() {
    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory)
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

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        return true
    }

    override fun updateFuelState(boat: IControllable, state: NBTTagCompound, inv: IInventory) {
        // NOP
    }

    override fun getFuelTime(fuelItem: Item): Int {
        return 0
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
    }

}