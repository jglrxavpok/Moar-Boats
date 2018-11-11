package org.jglrxavpok.moarboats.integration.opencomputers

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.integration.opencomputers.client.GuiComputerModule

object ComputerModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "occomputer")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) { }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory, true)

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiComputerModule(player, boat)
}