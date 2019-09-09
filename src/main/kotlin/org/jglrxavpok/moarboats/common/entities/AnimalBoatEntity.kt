package org.jglrxavpok.moarboats.common.entities

import net.minecraft.block.state.IBlockState
import net.minecraft.dispenser.IDispenseItemBehavior
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.EntityWaterMob
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityDispenser
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.items.AnimalBoatItem
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.toRadians
import java.util.*

class AnimalBoatEntity(level: World): BasicBoatEntity(EntityEntries.AnimalBoat, level) {
    override val entityID: Int
        get() = entityId

    override val modules: List<BoatModule> = emptyList()
    override val moduleRNG: Random = Random()

    init {
        this.preventEntitySpawning = true
        this.setSize(1.375f *1.5f, 0.5625f)
    }

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPosition(x, y, z)
        this.motionX = 0.0
        this.motionY = 0.0
        this.motionZ = 0.0
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
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
        val list = this.level.getEntitiesInAABBexcluding(this, this.boundingBox.grow(0.20000000298023224, 0.009999999776482582, 0.20000000298023224), EntitySelectors.pushableBy(this))

        for (entity in list) {
            if (!entity.isPassenger(this)) {
                if (this.passengers.isEmpty() && entity.ridingEntity == null && entity.width < this.width && entity is LivingEntity && entity !is EntityWaterMob && entity !is PlayerEntity) {
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

    override fun calculateAnchorPosition(linkType: Int): Vec3d {
        val distanceFromCenter = (0.0625f * 17f * if(linkType == BasicBoatEntity.FrontLink) 1f else -1f) *1.5f
        val anchorX = positionX + MathHelper.cos((yaw + 90f).toRadians()) * distanceFromCenter
        val anchorY = positionY + 0.0625f * 16f
        val anchorZ = positionZ + MathHelper.sin((yaw + 90f).toRadians()) * distanceFromCenter
        return Vec3d(anchorX, anchorY, anchorZ)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            entityDropItem(AnimalBoatItem, 1)
        }
    }

    override fun isValidLiquidBlock(pos: BlockPos) = Fluids.isUsualLiquidBlock(level, pos)

    override fun attackEntityFrom(source: DamageSource, amount: Float) = when(source) {
        DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ON_FIRE -> false
        is EntityDamageSourceIndirect -> false // avoid to kill yourself with your own arrows; also you are an *iron* boat, act like it
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
        return TileEntityDispenser() as T
    }

    override fun getX() = posX

    override fun getY() = posY

    override fun getZ() = posZ

    override fun getBlockState(): IBlockState {
        return level.getBlockState(position)
    }

    override fun getBlockPos(): BlockPos {
        return position
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
}