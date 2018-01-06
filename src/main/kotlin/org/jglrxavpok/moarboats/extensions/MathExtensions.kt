package org.jglrxavpok.moarboats.extensions

import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.util.vector.Quaternion
import net.minecraft.world.gen.structure.StructureMineshaftPieces.Cross
import java.lang.Math





fun Float.toRadians() = this / 180f * Math.PI.toFloat()
fun Double.toRadians() = this / 360.0 * Math.PI * 2.0
fun Double.toDegrees() = this * 360.0 / Math.PI / 2.0
fun Float.toDegrees() = this * 360.0f / Math.PI.toFloat() / 2f

fun Quaternion.lookAt(x: Double, y: Double, z: Double) {
    val length = Math.sqrt(x*x + y*y + z*z)
    val forwardVector = Vec3d(x/length, y/length, z/length)
    val dot = -forwardVector.z // forward is along Z

    when {
        Math.abs(dot - (-1.0f)) < 0.000001f -> set(0f, 1f, 0f, Math.PI.toFloat())
        Math.abs(dot - (1.0f)) < 0.000001f -> setIdentity()
        else -> {
            // FIXME: don't use Vec3d and directly calculate quaternion
            val angle = Math.acos(dot)
            val axis = Vec3d(0.0, 0.0, -1.0).crossProduct(forwardVector).normalize()
            val halfAngle = angle * .5f
            val s = -Math.sin(halfAngle)
            this.x = (axis.x * s).toFloat()
            this.y = (axis.y * s).toFloat()
            this.z = (axis.z * s).toFloat()
            this.w = Math.cos(halfAngle).toFloat()
        }
    }
}