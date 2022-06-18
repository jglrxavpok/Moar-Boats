package org.jglrxavpok.moarboats.extensions

fun Float.toRadians() = this / 180f * Math.PI.toFloat()
fun Double.toRadians() = this / 360.0 * Math.PI * 2.0
fun Double.toDegrees() = this * 360.0 / Math.PI / 2.0
fun Float.toDegrees() = this * 180.0f / Math.PI.toFloat()

// Forge Units
val Int.k get() = this * 1000
val Int.M get() = this * 1_000_000