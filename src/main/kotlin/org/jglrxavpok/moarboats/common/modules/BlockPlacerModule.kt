package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiBlockPlacer
import org.jglrxavpok.moarboats.common.containers.ContainerBlockPlacer

object BlockPlacerModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "block_placer")
    override val usesInventory = true
    override val moduleSpot = Spot.Misc

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) {

    }

    override fun update(from: IControllable) {
      //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddition(to: IControllable) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = ContainerBlockPlacer(player.inventory, this, boat)

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiBlockPlacer(player.inventory, this, boat)
    }
}