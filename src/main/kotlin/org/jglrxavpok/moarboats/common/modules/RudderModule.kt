package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.init.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.client.gui.GuiRudderModule
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.items.RudderItem
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import org.jglrxavpok.moarboats.common.state.FloatBoatProperty

object RudderModule: BoatModule(), BlockReason {
    override val id = ResourceLocation(MoarBoats.ModID, "rudder")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation
    override val isMenuInteresting = true

    val RudderAngleMultiplier = FloatBoatProperty("rudderAngleMultiplier")
    val BlockingProperty = BooleanBoatProperty("blocking")

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) {
        RudderAngleMultiplier[from] = 0f
        val controllingEntity = from.correspondingEntity.controllingPassenger as? PlayerEntity ?: return
        val forward = controllingEntity.moveForward
        val strafe = controllingEntity.moveStrafing
        if(forward <= 0.001f) {
            if(BlockingProperty[from])
                from.blockMovement(this)
        } else {
            //from.accelerate(forward)
        }
        from.turnLeft(strafe)
        RudderAngleMultiplier[from] = strafe
    }

    override fun blocksRotation() = false

    override fun update(from: IControllable) { }

    override fun onAddition(to: IControllable) {
        BlockingProperty[to] = true
    }

    override fun createContainer(player: PlayerEntity, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory, isLarge = false)
    }

    override fun createGui(player: PlayerEntity, boat: IControllable): Screen {
        return GuiRudderModule(player.inventory, this, boat)
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(RudderItem, 1)
    }
}