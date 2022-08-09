package org.jglrxavpok.moarboats.common.vanillaglue

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.api.Link
import org.jglrxavpok.moarboats.common.Cleats
import org.jglrxavpok.moarboats.extensions.toDegrees
import kotlin.math.sqrt

/**
 * Capability for entities to allow for cleats to be attached
 */
abstract class ICleatCapability(): ICapabilityProvider, ICleatLinkStorage {

    fun linkTo(other: Entity?, cleatOnThis: Cleat, cleatOnOther: Cleat?) {
        val currentLinks = mutableMapOf<Cleat, Link>()
        currentLinks.putAll(getLinkStorage())

        val link = getLink(cleatOnThis)
        if(cleatOnOther != null) {
            link.linkTo(cleatOnOther, other!!)
        } else {
            link.reset()
        }

        syncLinkStorage(currentLinks)
    }

    fun hasLinkAt(cleatType: Cleat) = getLink(cleatType).hasTarget()

    fun getLink(cleat: Cleat): Link = getLinkStorage().computeIfAbsent(cleat, ::Link)

    fun getLinkedTo(cleat: Cleat): Entity? {
        return getLink(cleat).targetEntity
    }

    fun tick(level: Level, owner: Entity) {
        getLinkStorage().forEach { cleat, link ->
            link.makeRuntimeRepresentation(level, owner)

            breakLinkIfNeeded(cleat)

            if(cleat.canTow())
                return@forEach

            val heading = link.targetEntity
            if(heading != null) {
                val anchorPos = cleat.getWorldPosition(owner)
                val otherAnchorPos = link.target!!.getWorldPosition(heading)

                val alpha = 0.5f

                val restingLength = 0.5f
                val d0 = (otherAnchorPos.x - anchorPos.x)
                val d1 = (otherAnchorPos.y - anchorPos.y)
                val d2 = (otherAnchorPos.z - anchorPos.z)
                val length = sqrt(d0 * d0 + d1 * d1 + d2 * d2).coerceAtLeast(0.01)

                if(length >= restingLength) {
                    val k = -0.03
                    val forceMagnitude = -k * (length - restingLength)
                    val dirX = d0 / length
                    val dirY = d1 / length
                    val dirZ = d2 / length

                    val dampingFactor = 0.2;
                    val dampingX = owner.deltaMovement.x * -dampingFactor;
                    val dampingZ = owner.deltaMovement.z * -dampingFactor;
                    owner.deltaMovement = owner.deltaMovement.add(dirX * forceMagnitude + dampingX, dirY * forceMagnitude, dirZ * forceMagnitude + dampingZ)

                    // FIXME: handle case where targetYaw is ~0-180 and yRot is ~180+ (avoid doing a crazy flip)
                    val targetYaw = computeTargetYaw(owner.yRot, anchorPos, otherAnchorPos)
                    owner.yRot = alpha * owner.yRot + targetYaw * (1f - alpha)
                }
            }
        }
    }

    private fun computeTargetYaw(currentYaw: Float, anchorPos: Vec3, otherAnchorPos: Vec3): Float {
        val idealYaw = Math.atan2(otherAnchorPos.x - anchorPos.x, -(otherAnchorPos.z - anchorPos.z)).toFloat().toDegrees() + 180f
        var closestDistance = Float.POSITIVE_INFINITY
        var closest = idealYaw
        for(sign in -1..1) {
            val potentialYaw = idealYaw + sign * 360f
            val distance = Math.abs(potentialYaw - currentYaw)
            if(distance < closestDistance) {
                closestDistance = distance
                closest = potentialYaw
            }
        }
        return closest
    }

    private fun breakLinkIfNeeded(cleatType: Cleat) {
        val link = getLink(cleatType)
        if(link.hasRuntimeTarget() && !link.targetEntity!!.isAlive) {
            linkTo(null, cleatType, null)
        }
    }

    fun readFromNBT(compound: CompoundTag) {
        getLinkStorage().clear()
        val linkList = compound.getList("links", CompoundTag.TAG_COMPOUND.toInt())
        for(linkData in linkList) {
            linkData as CompoundTag
            val originLocation = ResourceLocation(linkData.getString("origin_cleat"))
            val originCleat = Cleats.Registry.get().getValue(originLocation) ?: error("Unregistered origin cleat $originLocation for entity $this")
            getLink(originCleat).readNBT(linkData)
        }
        syncLinkStorage(getLinkStorage())
    }

    fun saveToNBT(compound: CompoundTag) {
        val linkList = ListTag()
        for(link in getLinkStorage()) {
            val linkCompound = CompoundTag()
            linkCompound.putString("origin_cleat", Cleats.Registry.get().getKey(link.key)?.toString() ?: error("Unknown cleat type: ${link.key}"))
            link.value.writeNBT(linkCompound)
            linkList.add(linkCompound)
        }
        compound.put("links", linkList)
    }

    // boilerplate

    companion object {
        val ResourceID = ResourceLocation(MoarBoats.ModID, "cleat_capability")
        val Capability = CapabilityManager.get(object : CapabilityToken<ICleatCapability>() {})
    }

    val holder: LazyOptional<ICleatCapability> = LazyOptional.of { this };

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return Capability.orEmpty(cap, holder)
    }
}