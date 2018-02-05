package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.Container
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiSeatModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.items.SeatItem

object SeatModule : BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "seat")

    override val usesInventory = false
    override val moduleSpot = Spot.Storage

    @SideOnly(Side.CLIENT)
    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiSeatModule(player.inventory, this, boat)
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container? {
        return EmptyContainer(player.inventory)
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) { }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(SeatItem, 1)
    }
}