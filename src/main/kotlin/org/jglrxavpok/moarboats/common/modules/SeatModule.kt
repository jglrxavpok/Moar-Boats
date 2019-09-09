package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.items.SeatItem

object SeatModule : BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "seat")

    override val usesInventory = false
    override val moduleSpot = Spot.Storage
    override val isMenuInteresting = false

    @OnlyIn(Dist.CLIENT)
    override fun createGui(player: PlayerEntity, boat: IControllable): Screen {
        return GuiNoConfigModule(player.inventory, this, boat)
    }

    override fun createContainer(player: PlayerEntity, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory)
    }

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) { }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(SeatItem, 1)
    }
}