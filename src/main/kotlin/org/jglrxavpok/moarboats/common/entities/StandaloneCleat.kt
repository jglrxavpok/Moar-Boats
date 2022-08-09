package org.jglrxavpok.moarboats.common.entities

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.network.PlayMessages
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.api.Link
import org.jglrxavpok.moarboats.common.BoatLinksSerializer
import org.jglrxavpok.moarboats.common.Cleats
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatCapability
import org.jglrxavpok.moarboats.extensions.toDegrees
import kotlin.math.sqrt

class StandaloneCleat(type: EntityType<out StandaloneCleat>, level: Level): Entity(type, level), IEntityAdditionalSpawnData {

    companion object {
        // Yes, Boat::class and not StandaloneCleat, that's on purpose
        @JvmField
        val BOAT_LINKS = SynchedEntityData.defineId(Boat::class.java, BoatLinksSerializer)


        val ParentEntityID = SynchedEntityData.defineId(StandaloneCleat::class.java, EntityDataSerializers.INT)
        val NoParent = 0
    }

    lateinit var cleatType: Cleat

    constructor(type: EntityType<out StandaloneCleat>, level: Level, cleat: Cleat, parent: Entity): this(type, level) {
        this.cleatType = cleat
        setParent(parent)

        setPos(parent.position())
    }

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(EntityEntries.StandaloneCleat.get(), level) {

    }

    fun setParent(parent: Entity) {
        setParentID(parent.id)
    }

    private fun setParentID(id: Int) {
        entityData.set(ParentEntityID, id)
    }

    private fun getParentID(): Int {
        return entityData.get(ParentEntityID)
    }

    fun getParent(): Entity? {
        val parentID = getParentID()
        if(parentID == NoParent) {
            return null
        }

        return level.getEntity(parentID)
    }

    fun clear() {
        setParentID(NoParent)
    }

    override fun defineSynchedData() {
        entityData.define(ParentEntityID, NoParent)
    }

    override fun tick() {
        super.tick()

        val parent = getParent()
        if(parent == null) {
            remove(RemovalReason.DISCARDED)
            return
        }

        if(parent.isRemoved) {
            remove(RemovalReason.DISCARDED)
            return
        }

        val parentCleatCapability = parent.getCapability(ICleatCapability.Capability).orElseThrow{ IllegalStateException("Attached a cleat to an entity that has no cleat capability?") }
        parentCleatCapability.tick(level, parent)

        // 1. set position
        setPos(cleatType.getWorldPosition(parent, 0.0f))
        setOldPosAndRot()
        setPos(cleatType.getWorldPosition(parent, 1.0f))
        deltaMovement = parent.deltaMovement
    }

    override fun readAdditionalSaveData(p_20052_: CompoundTag) {
    }

    override fun addAdditionalSaveData(p_20139_: CompoundTag) {
    }

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        buffer.writeVarInt(getParentID());
        buffer.writeResourceLocation(Cleats.Registry.get().getKey(cleatType))
    }

    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        setParentID(additionalData.readVarInt());
        val cleatID = additionalData.readResourceLocation()
        val wantedCleat = Cleats.Registry.get().getValue(cleatID)
        if(wantedCleat == null) {
            MoarBoats.logger.error("Invalid cleat type: {}", cleatID)
            remove(RemovalReason.DISCARDED)
            return
        }
        cleatType = wantedCleat
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        val itemstack = player.getItemInHand(hand)
        // TODO: check with dedicated server
        if(itemstack.item is RopeItem && !level.isClientSide) {
            val parentEntity = getParent() ?: return InteractionResult.FAIL
            RopeItem.onLinkUsed(itemstack, player, hand, level, parentEntity, cleatType)
            return InteractionResult.SUCCESS
        }
        return InteractionResult.FAIL
    }

    override fun shouldBeSaved(): Boolean {
        return false
    }

    override fun isPickable(): Boolean {
        return true
    }
}
