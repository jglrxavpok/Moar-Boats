package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.items.DivingBottleItem
import org.jglrxavpok.moarboats.extensions.getEntities

object DivingModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "diving")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc
    override val isMenuInteresting = false

    val maxDistSq = 20.0*20.0

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        val world = from.worldRef
        val entities = world.getEntities<LivingEntity>(null) { entity ->
            val correctDistance = entity?.distanceToSqr(from.correspondingEntity) ?: Double.POSITIVE_INFINITY <= maxDistSq
            val inWater = entity?.isInWater ?: false
            inWater && correctDistance
        }.map { it as LivingEntity }
        entities.forEach {
            it.addEffect(EffectInstance(Effects.WATER_BREATHING, 2, 1, true, true))
        }
    }

    override fun onAddition(to: IControllable) { }

    override fun createContainer(containerID: Int, player: PlayerEntity, boat: IControllable): ContainerBoatModule<*>? = EmptyModuleContainer(containerID, player.inventory, this, boat)

    override fun createGui(containerID: Int, player: PlayerEntity, boat: IControllable) = GuiNoConfigModule(containerID, player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(DivingBottleItem, 1)
    }
}
