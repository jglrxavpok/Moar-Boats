package org.jglrxavpok.moarboats.api

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.dispenser.DispenseItemBehavior
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.modules.BlockReason
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.toRadians
import java.util.*

interface IControllable {

    val entityID: Int
    val modules: List<BoatModule>
    val worldRef: Level
    val positionX: Double
    val positionY: Double
    val positionZ: Double
    val velocityX: Double
    val velocityY: Double
    val velocityZ: Double
    val yaw: Float
    val correspondingEntity: Entity
    val moduleRNG: RandomSource
    val blockedReason: BlockReason
    val imposedSpeed: Float

    val world: Level get()= worldRef

    fun inLiquid(): Boolean
    fun isEntityInLava(): Boolean

    fun turnRight(multiplier: Float = 1f)

    fun turnLeft(multiplier: Float = 1f)
    fun accelerate(multiplier: Float = 1f)
    fun decelerate(multiplier: Float = 1f)
    fun blockMovement(blockedReason: BlockReason)

    /**
     * If 'isLocal' = true, then state will not be synchronised between client & server nor will it be saved to the disk
     */
    fun saveState(module: BoatModule, isLocal: Boolean = false)

    /**
     * If 'isLocal' = true, then state will not be synchronised between client & server nor will it be saved to the disk
     */
    fun getState(module: BoatModule, isLocal: Boolean = false): CompoundTag

    fun getInventory(module: BoatModule): BoatModuleInventory

    fun dispense(behavior: DispenseItemBehavior, stack: ItemStack, overridePosition: BlockPos? = null, overrideFacing: Direction? = null): ItemStack

    /**
     * Takes into account the rotation of the boat
     */
    fun reorientate(overrideFacing: Direction): Direction

    fun getOwnerIdOrNull(): UUID?
    fun getOwnerNameOrNull(): String?

    fun isSpeedImposed(): Boolean
    fun imposeSpeed(speed: Float)

    fun calculateAnchorPosition(linkType: Int): Vec3 {
        val distanceFromCenter = 0.0625f * 17f * if(linkType == BasicBoatEntity.FrontLink) 1f else -1f
        val anchorX = positionX + Mth.cos((yaw + 90f).toRadians()) * distanceFromCenter
        val anchorY = positionY + 4.0 / 16.0 + 0.375 + if(correspondingEntity.isInLava) 0.20 else 0.0
        val anchorZ = positionZ + Mth.sin((yaw + 90f).toRadians()) * distanceFromCenter
        return Vec3(anchorX, anchorY, anchorZ)
    }

    /**
     * Applies current yaw rotation to the vector
     */
    fun localToWorld(localVec: Vec3): Vec3 {
        return localVec.yRot((180f - yaw).toRadians()).add(positionX, positionY, positionZ)
    }

    fun sortModulesByInterestingness(): Iterable<BoatModule> {
        // automatically put non-interesting menus at the bottom of the tab list
        return modules.sortedBy {
            if(it.isMenuInteresting) {
                it.moduleSpot.ordinal
            } else {
                +100
            }
        }
    }

    /**
     * Forces a chunk to be force loaded. Will be effective for 10s. All forced chunks are unforced when the boat is destroyed
     */
    fun forceChunkLoad(x: Int, z: Int) {
        throw UnsupportedOperationException("This ($this) boat does not support chunk force-loading")
    }

    operator fun <T> contains(property: BoatProperty<T>): Boolean
}
