package org.jglrxavpok.moarboats.extensions

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.util.vector.Quaternion
import net.minecraft.world.gen.structure.StructureMineshaftPieces.Cross
import java.lang.Math

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

fun Quaternion.lookAt(x: Double, y: Double, z: Double) {
    val length = Math.sqrt(x*x + y*y + z*z)
    val forwardVector = Vec3d(x/length, y/length, z/length)
    val dot = forwardVector.z // forward is along Z

    when {
        Math.abs(dot - (-1.0f)) < 0.000001f -> set(0f, 1f, 0f, Math.PI.toFloat())
        Math.abs(dot - (1.0f)) < 0.000001f -> setIdentity()
        else -> {
            // FIXME: don't use Vec3d and directly calculate quaternion
            val angle = Math.acos(dot)
            val axis = Vec3d(0.0, 0.0, -1.0).crossProduct(forwardVector).normalize()
            val halfAngle = angle * .5f
            val s = Math.sin(halfAngle)
            this.x = (axis.x * s).toFloat()
            this.y = (axis.y * s).toFloat()
            this.z = (axis.z * s).toFloat()
            this.w = Math.cos(halfAngle).toFloat()
        }
    }
}

fun Quaternion.setLookAlong(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float) {
    setIdentity()
    // Normalize direction
    val invDirLength = (1.0 / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble())).toFloat()
    val dirnX = -dirX * invDirLength
    val dirnY = -dirY * invDirLength
    val dirnZ = -dirZ * invDirLength
    // left = up x dir
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirnZ - upZ * dirnY
    leftY = upZ * dirnX - upX * dirnZ
    leftZ = upX * dirnY - upY * dirnX
    // normalize left
    val invLeftLength = (1.0 / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble())).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirnY * leftZ - dirnZ * leftY
    val upnY = dirnZ * leftX - dirnX * leftZ
    val upnZ = dirnX * leftY - dirnY * leftX

    /* Convert orthonormal basis vectors to quaternion */
    val x: Float
    val y: Float
    val z: Float
    val w: Float
    var t: Double
    val tr = (leftX + upnY + dirnZ).toDouble()
    if (tr >= 0.0) {
        t = Math.sqrt(tr + 1.0)
        w = (t * 0.5).toFloat()
        t = 0.5 / t
        x = ((dirnY - upnZ) * t).toFloat()
        y = ((leftZ - dirnX) * t).toFloat()
        z = ((upnX - leftY) * t).toFloat()
    } else {
        if (leftX > upnY && leftX > dirnZ) {
            t = Math.sqrt(1.0 + leftX - upnY.toDouble() - dirnZ.toDouble())
            x = (t * 0.5).toFloat()
            t = 0.5 / t
            y = ((leftY + upnX) * t).toFloat()
            z = ((dirnX + leftZ) * t).toFloat()
            w = ((dirnY - upnZ) * t).toFloat()
        } else if (upnY > dirnZ) {
            t = Math.sqrt(1.0 + upnY - leftX.toDouble() - dirnZ.toDouble())
            y = (t * 0.5).toFloat()
            t = 0.5 / t
            x = ((leftY + upnX) * t).toFloat()
            z = ((upnZ + dirnY) * t).toFloat()
            w = ((leftZ - dirnX) * t).toFloat()
        } else {
            t = Math.sqrt(1.0 + dirnZ - leftX.toDouble() - upnY.toDouble())
            z = (t * 0.5).toFloat()
            t = 0.5 / t
            x = ((dirnX + leftZ) * t).toFloat()
            y = ((upnZ + dirnY) * t).toFloat()
            w = ((upnX - leftY) * t).toFloat()
        }
    }
    /* Multiply */
    set(this.w * x + this.x * w + this.y * z - this.z * y,
            this.w * y - this.x * z + this.y * w + this.z * x,
            this.w * z + this.x * y - this.y * x + this.z * w,
            this.w * w - this.x * x - this.y * y - this.z * z)
}