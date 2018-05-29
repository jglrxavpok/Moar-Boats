package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

object RudderModule: BoatModule(), BlockReason {
    override val id = ResourceLocation(MoarBoats.ModID, "rudder")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) {
        val controllingEntity = from.correspondingEntity.controllingPassenger as? EntityPlayer ?: return
        val forward = controllingEntity.moveForward
        val strafe = controllingEntity.moveStrafing
        if(forward <= 0.001f) {
            from.blockMovement(this)
        } else {
            //from.accelerate(forward)
        }
        from.turnLeft(strafe)
    }

    override fun blocksRotation() = false

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) { }

    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory, isLarge = false)
    }

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiNoConfigModule(player.inventory, this, boat)
    }
}