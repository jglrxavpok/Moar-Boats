package org.jglrxavpok.moarboats.common.entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.dispenser.DispenseItemBehavior
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.*
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.toRadians
import java.util.*

class AnimalBoatEntity(entityType: EntityType<out AnimalBoatEntity>, world: Level): BasicBoatEntity(entityType, world) {
    override val entityID: Int
        get() = this.id

    override val modules: List<BoatModule> = emptyList()
    override val moduleRNG = RandomSource.create()

    init {
        this.blocksBuilding = true
    }

    constructor(entityType: EntityType<out AnimalBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun getBoatItem() = MBItems.AnimalBoat.get()

    override fun getOwnerIdOrNull(): UUID? {
        return null
    }

    override fun getOwnerNameOrNull(): String? {
        return null
    }

    override fun getPassengersRidingOffset(): Double {
        return 0.0
    }

    override fun tick() {
        super.tick()
        val list = this.world.getEntities(this, this.boundingBox.expandTowards(0.20000000298023224, 0.009999999776482582, 0.20000000298023224), EntitySelector.pushableBy(this))

        for (entity in list) {
            if (!entity.hasPassenger(this)) {
                if (this.passengers.isEmpty() && !entity.isPassenger && entity.bbWidth < this.bbWidth && entity is LivingEntity && entity !is WaterAnimal && entity !is Player) {
                    entity.startRiding(this)
                }
            }
        }
    }

    override fun isPushable(): Boolean {
        return false
    }

    // MoarBoats code
    override fun controlBoat() { /* NOP */ }

    override fun calculateAnchorPosition(linkType: Int): Vec3 {
        val distanceFromCenter = (0.0625f * 17f * if(linkType == BasicBoatEntity.FrontLink) 1f else -1f) *1.5f
        val anchorX = positionX + Mth.cos((yaw + 90f).toRadians()) * distanceFromCenter
        val anchorY = positionY + 0.0625f * 16f
        val anchorZ = positionZ + Mth.sin((yaw + 90f).toRadians()) * distanceFromCenter
        return Vec3(anchorX, anchorY, anchorZ)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            spawnAtLocation(getBoatItem(), 1)
        }
    }

    override fun isValidLiquidBlock(pos: BlockPos) = Fluids.isUsualLiquidBlock(world, pos)

    override fun hurt(source: DamageSource, amount: Float) = when(source) {
        DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ON_FIRE -> false
        is IndirectEntityDamageSource -> false // avoid to kill yourself with your own arrows; also you are an *iron* boat, act like it
        else -> super.hurt(source, amount)
    }

    override fun canStartRiding(player: Player, heldItem: ItemStack, hand: InteractionHand): Boolean {
        return false
    }

    override fun saveState(module: BoatModule, isLocal: Boolean) { /* NOP */ }

    override fun getState(module: BoatModule, isLocal: Boolean): CompoundTag {
        return CompoundTag()
    }

    override fun getInventory(module: BoatModule): BoatModuleInventory {
        error("Animal boats cannot have inventories!")
    }

    override fun dispense(behavior: DispenseItemBehavior, stack: ItemStack, overridePosition: BlockPos?, overrideFacing: Direction?): ItemStack {
        return ItemStack.EMPTY
    }

    override fun reorientate(overrideFacing: Direction): Direction {
        return overrideFacing
    }

    override fun positionRider(passenger: Entity) {
        if (this.hasPassenger(passenger)) {
            val f1 = ((if ( ! this.isAlive) 0.009999999776482582 else this.myRidingOffset) + passenger.passengersRidingOffset).toFloat()

            passenger.setPos(this.x, this.y + f1.toDouble(), this.z)
            passenger.yRot += this.deltaRotation
            passenger.yHeadRot = passenger.yHeadRot + this.deltaRotation
            this.applyYawToEntity(passenger)
        }
    }

    override fun canAddPassenger(passenger: Entity): Boolean {
        return this.passengers.isEmpty() && passenger !is Player
    }

    override fun isSpeedImposed(): Boolean {
        return false
    }

    override fun imposeSpeed(speed: Float) { }

    override fun <T> contains(property: BoatProperty<T>): Boolean {
        return false
    }

    override fun openGuiIfPossible(player: Player): InteractionResult {
        return InteractionResult.FAIL
    }
}
