package org.jglrxavpok.moarboats.common.entities

import net.minecraft.block.BlockState
import net.minecraft.dispenser.IDispenseItemBehavior
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.WaterMobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.DispenserTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.items.AnimalBoatItem
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.toRadians
import java.util.*

class AnimalBoatEntity(world: World): BasicBoatEntity(EntityEntries.AnimalBoat, world) {
    override val entityID: Int
        get() = this.entityId

    override val modules: List<BoatModule> = emptyList()
    override val moduleRNG: Random = Random()

    init {
        this.preventEntitySpawning = true
    }

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPosition(x, y, z)
        this.motion = Vector3d.ZERO
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
    }

    override fun getWorld(): World {
        return this.worldRef
    }

    override fun getBoatItem() = AnimalBoatItem

    override fun getOwnerIdOrNull(): UUID? {
        return null
    }

    override fun getOwnerNameOrNull(): String? {
        return null
    }

    override fun getMountedYOffset(): Double {
        return 0.0
    }

    override fun tick() {
        super.tick()
        val list = this.world.getEntitiesInAABBexcluding(this, this.boundingBox.expand(0.20000000298023224, 0.009999999776482582, 0.20000000298023224), EntityPredicates.pushableBy(this))

        for (entity in list) {
            if (!entity.isPassenger(this)) {
                if (this.passengers.isEmpty() && !entity.isPassenger && entity.width < this.width && entity is LivingEntity && entity !is WaterMobEntity && entity !is PlayerEntity) {
                    entity.startRiding(this)
                }
            }
        }
    }

    override fun canBePushed(): Boolean {
        return false
    }

    // MoarBoats code
    override fun controlBoat() { /* NOP */ }

    override fun calculateAnchorPosition(linkType: Int): Vector3d {
        val distanceFromCenter = (0.0625f * 17f * if(linkType == BasicBoatEntity.FrontLink) 1f else -1f) *1.5f
        val anchorX = positionX + MathHelper.cos((yaw + 90f).toRadians()) * distanceFromCenter
        val anchorY = positionY + 0.0625f * 16f
        val anchorZ = positionZ + MathHelper.sin((yaw + 90f).toRadians()) * distanceFromCenter
        return Vector3d(anchorX, anchorY, anchorZ)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            entityDropItem(AnimalBoatItem, 1)
        }
    }

    override fun isValidLiquidBlock(pos: BlockPos) = Fluids.isUsualLiquidBlock(world, pos)

    override fun attackEntityFrom(source: DamageSource, amount: Float) = when(source) {
        DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ON_FIRE -> false
        is IndirectEntityDamageSource -> false // avoid to kill yourself with your own arrows; also you are an *iron* boat, act like it
        else -> super.attackEntityFrom(source, amount)
    }

    override fun canStartRiding(player: PlayerEntity, heldItem: ItemStack, hand: Hand): Boolean {
        return false
    }

    override fun saveState(module: BoatModule, isLocal: Boolean) { /* NOP */ }

    override fun getState(module: BoatModule, isLocal: Boolean): CompoundNBT {
        return CompoundNBT()
    }

    override fun getInventory(module: BoatModule): BoatModuleInventory {
        error("Animal boats cannot have inventories!")
    }

    override fun dispense(behavior: IDispenseItemBehavior, stack: ItemStack, overridePosition: BlockPos?, overrideFacing: Direction?): ItemStack {
        return ItemStack.EMPTY
    }

    override fun reorientate(overrideFacing: Direction): Direction {
        return overrideFacing
    }

    override fun <T : TileEntity?> getBlockTileEntity(): T {
        return DispenserTileEntity() as T
    }

    override fun getX() = posX

    override fun getY() = posY

    override fun getZ() = posZ

    override fun getBlockState(): BlockState {
        return world.getBlockState(blockPos)
    }

    override fun getBlockPos(): BlockPos {
        return super.func_233580_cy_()// TODO: check correct
    }

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            val f1 = ((if ( ! this.isAlive) 0.009999999776482582 else this.mountedYOffset) + passenger.yOffset).toFloat()

            passenger.setPosition(this.posX, this.posY + f1.toDouble(), this.posZ)
            passenger.rotationYaw += this.deltaRotation
            passenger.rotationYawHead = passenger.rotationYawHead + this.deltaRotation
            this.applyYawToEntity(passenger)
        }
    }

    override fun canFitPassenger(passenger: Entity): Boolean {
        return this.passengers.isEmpty() && passenger !is PlayerEntity
    }

    override fun isSpeedImposed(): Boolean {
        return false
    }

    override fun imposeSpeed(speed: Float) { }

    override fun <T> contains(property: BoatProperty<T>): Boolean {
        return false
    }

    override fun openGuiIfPossible(player: PlayerEntity): ActionResultType {
        return ActionResultType.FAIL
    }
}
