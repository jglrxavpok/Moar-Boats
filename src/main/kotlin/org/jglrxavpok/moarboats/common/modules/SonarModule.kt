package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiSonarModule
import org.jglrxavpok.moarboats.common.containers.ContainerBase

object SonarModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "sonar")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        // TODO
    }

    override fun update(from: IControllable) {
        // TODO
    }

    override fun onAddition(to: IControllable) {
        // TODO
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase? {
        return null
    }

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiSonarModule(player.inventory, this, boat)
}