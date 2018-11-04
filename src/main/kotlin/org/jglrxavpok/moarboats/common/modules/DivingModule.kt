package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.potion.PotionType
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

object DivingModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "diving")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc
    override val isMenuInteresting = false

    val maxDistSq = 20.0*20.0

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        val world = from.worldRef
        val entities = world.getEntities(EntityLivingBase::class.java) { entity ->
            val correctDistance = entity?.getDistanceSq(from.correspondingEntity) ?: Double.POSITIVE_INFINITY <= maxDistSq
            val inWater = entity?.isInWater ?: false
            inWater && correctDistance
        }
        entities.forEach {
            it.addPotionEffect(PotionEffect(MobEffects.WATER_BREATHING, 2, 1, true, true))
        }
    }

    override fun onAddition(to: IControllable) { }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiNoConfigModule(player.inventory, this, boat)
}