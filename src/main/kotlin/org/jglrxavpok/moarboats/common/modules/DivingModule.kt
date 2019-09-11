package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.items.DivingBottleItem

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
        val entities = world.getEntities(LivingEntity::class.java) { entity ->
            val correctDistance = entity?.getDistanceSq(from.correspondingEntity) ?: Double.POSITIVE_INFINITY <= maxDistSq
            val inWater = entity?.isInWater ?: false
            inWater && correctDistance
        }
        entities.forEach {
            it.addPotionEffect(PotionEffect(MobEffects.WATER_BREATHING, 2, 1, true, true))
        }
    }

    override fun onAddition(to: IControllable) { }

    override fun createContainer(player: PlayerEntity, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: PlayerEntity, boat: IControllable) = GuiNoConfigModule(player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(DivingBottleItem, 1)
    }
}
