package org.jglrxavpok.moarboats.api

import net.minecraft.dispenser.IBlockSource
import net.minecraft.dispenser.IDispenseItemBehavior
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.modules.BlockReason
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.toRadians
import java.util.*

interface IControllable: IBlockSource {

    val entityID: Int
    val modules: List<BoatModule>
    val worldRef: World
    val positionX: Double
    val positionY: Double
    val positionZ: Double
    val velocityX: Double
    val velocityY: Double
    val velocityZ: Double
    val yaw: Float
    val correspondingEntity: Entity
    val moduleRNG: Random
    val blockedReason: BlockReason
    val imposedSpeed: Float

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
    fun getState(module: BoatModule, isLocal: Boolean = false): CompoundNBT

    fun getInventory(module: BoatModule): BoatModuleInventory

    fun dispense(behavior: IDispenseItemBehavior, stack: ItemStack, overridePosition: BlockPos? = null, overrideFacing: Direction? = null): ItemStack

    /**
     * Takes into account the rotation of the boat
     */
    fun reorientate(overrideFacing: Direction): Direction

    fun getOwnerIdOrNull(): UUID?
    fun getOwnerNameOrNull(): String?

    fun isSpeedImposed(): Boolean
    fun imposeSpeed(speed: Float)

    fun calculateAnchorPosition(linkType: Int): Vector3d {
        val distanceFromCenter = 0.0625f * 17f * if(linkType == BasicBoatEntity.FrontLink) 1f else -1f
        val anchorX = positionX + MathHelper.cos((yaw + 90f).toRadians()) * distanceFromCenter
        val anchorY = positionY + 0.0625f * 16f
        val anchorZ = positionZ + MathHelper.sin((yaw + 90f).toRadians()) * distanceFromCenter
        return Vector3d(anchorX, anchorY, anchorZ)
    }

    /**
     * Applies current yaw rotation to the vector
     */
    fun localToWorld(localVec: Vector3d): Vector3d {
        return localVec.rotateYaw((180f - yaw).toRadians()).add(positionX, positionY, positionZ)
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
