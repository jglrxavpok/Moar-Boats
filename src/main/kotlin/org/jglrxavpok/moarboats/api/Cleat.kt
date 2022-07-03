package org.jglrxavpok.moarboats.api

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.jglrxavpok.moarboats.common.Cleats
import org.jglrxavpok.moarboats.extensions.toRadians
import java.util.*

/**
 * Location on a boat where a rope can be secured for towing or mooring
 */
abstract class Cleat {

    /**
     * Is this cleat used for towing other boats? If false, the boat can be towed *from* this cleat
     */
    abstract fun canTow(): Boolean

    /**
     * Can this cleat connect to the given other cleat?
     */
    abstract fun supportsConnection(other: Cleat): Boolean

    /**
     * Position of this cleat relative to the boat. Does not account for boat rotation
     */
    abstract fun getLocalPosition(): Vec3

    /**
     * Text displayed above the cleat when holding
     */
    abstract fun getOverlayText(): Component

    /**
     * Position of this cleat in world space, does account for boat rotation
     */
    fun getWorldPosition(owner: Entity): Vec3 {
        return getWorldPosition(owner, 1.0f)
    }

    /**
     * Position of this cleat in world space, does account for boat rotation.
     * Additional parameter for partial tick (to smooth rendering)
     */
    fun getWorldPosition(owner: Entity, partialTick: Float): Vec3 {
        val localPosition = getLocalPosition()

        val entityX = Mth.lerp(partialTick.toDouble(), owner.xOld, owner.x)
        val entityY = Mth.lerp(partialTick.toDouble(), owner.yOld, owner.y)
        val entityZ = Mth.lerp(partialTick.toDouble(), owner.zOld, owner.z)
        val yaw = Mth.lerp(partialTick, owner.yRotO, owner.yRot).toFloat()

        val angle = (90.0f + yaw).toRadians()
        val cosYaw = Mth.cos(angle)
        val sinYaw = Mth.sin(angle)
        val anchorX = entityX + cosYaw * localPosition.z + sinYaw * localPosition.x
        val anchorY = entityY + localPosition.y + 0.375 + if(owner.isInLava) 0.20 else 0.0
        val anchorZ = entityZ + sinYaw * localPosition.z + cosYaw * localPosition.x
        return Vec3(anchorX, anchorY, anchorZ)
    }
}

data class Link(val origin: Cleat, var target: Cleat? = null, var targetEntityUUID: UUID? = null) {

    companion object {
        val MaxSearchRadius = 100.0
    }

    /**
     * Runtime representation of the connected entity
     */
    var targetEntity: Entity? = null
        private set

    private fun checkState() {
        assert(target == null || (target != null && targetEntityUUID != null))
    }

    fun linkTo(other: Cleat, targetEntity: Entity) {
        target = other
        targetEntityUUID = targetEntity.uuid
        this.targetEntity = targetEntity
    }

    fun hasTarget() = target != null

    fun hasRuntimeTarget() = target != null && targetEntity != null

    fun makeRuntimeRepresentation(level: Level, owner: Entity) {
        if(target == null)
            return
        assert(targetEntityUUID != null) { "Target UUID can not be null if the target cleat is not null"}
        if(targetEntity != null)
            return
        val searchBB = owner.boundingBox.inflate(MaxSearchRadius)
        targetEntity = level.getEntities(owner, searchBB) { entity ->
            entity.uuid == targetEntityUUID
        }.firstOrNull()
    }

    fun reset() {
        target = null
        targetEntityUUID = null
        targetEntity = null
    }

    fun read(buf: FriendlyByteBuf) {
        reset()

        val connected = buf.readBoolean()
        if(!connected)
            return

        val cleatType = buf.readResourceLocation()
        target = Cleats.Registry.get().getValue(cleatType) ?: error("Unregistered cleat type $cleatType")
        targetEntityUUID = buf.readUUID()
        checkState()
    }

    fun write(buf: FriendlyByteBuf) {
        checkState()
        if(target == null) {
            buf.writeBoolean(false)
            return
        }

        buf.writeBoolean(true)
        buf.writeResourceLocation(Cleats.Registry.get().getKey(target)!!)
        buf.writeUUID(targetEntityUUID)
    }

    fun readNBT(input: CompoundTag) {
        reset()
        if(!input.contains("target_cleat"))
            return
        val cleatType = ResourceLocation(input.getString("target_cleat"))
        target = Cleats.Registry.get().getValue(cleatType) ?: error("Unregistered cleat type $cleatType")
        targetEntityUUID = input.getUUID("target_entity")
        checkState()
    }

    fun writeNBT(output: CompoundTag) {
        checkState()
        if(target == null)
            return
        val location = Cleats.Registry.get().getKey(target)
        output.putString("target_cleat", location.toString())
        output.putUUID("target_entity", targetEntityUUID!!)
    }
}