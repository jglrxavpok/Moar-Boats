package org.jglrxavpok.moarboats.common.modules

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.MenuTypes
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.items.DivingBottleItem
import org.jglrxavpok.moarboats.extensions.getEntities

object DivingModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "diving")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc
    override val isMenuInteresting = false

    val maxDistSq = 20.0*20.0

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        val world = from.worldRef
        val entities = world.getEntities<LivingEntity>(null) { entity ->
            val correctDistance = entity?.distanceToSqr(from.correspondingEntity) ?: Double.POSITIVE_INFINITY <= maxDistSq
            val inWater = entity?.isInWater ?: false
            inWater && correctDistance
        }.map { it as LivingEntity }
        entities.forEach {
            it.addEffect(MobEffectInstance(MobEffects.WATER_BREATHING, 2, 1, true, true))
        }
    }

    override fun onAddition(to: IControllable) { }

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? = EmptyModuleContainer(containerID, player.inventory, boat)
    override fun getMenuType() = ContainerTypes.EmptyModuleMenu.get()

    override fun createGui(containerID: Int, player: Player, boat: IControllable) = GuiNoConfigModule(containerID, player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(MBItems.DivingBottleItem.get(), 1)
    }
}
