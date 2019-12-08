package org.jglrxavpok.moarboats.extensions

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d

fun Float.toRadians() = this / 180f * Math.PI.toFloat()
fun Double.toRadians() = this / 360.0 * Math.PI * 2.0
fun Double.toDegrees() = this * 360.0 / Math.PI / 2.0
fun Float.toDegrees() = this * 180.0f / Math.PI.toFloat()

/**
 * Stricly equivalent to AxisAlignedBB#getCenter but also available to servers
 *
 * Thanks Mojang...
 */
fun AxisAlignedBB.getCenterForAllSides() = Vec3d(this.minX + (this.maxX - this.minX) * 0.5, this.minY + (this.maxY - this.minY) * 0.5, this.minZ + (this.maxZ - this.minZ) * 0.5)

// Forge Units
val Int.k get() = this * 1000
val Int.M get() = this * 1_000_000