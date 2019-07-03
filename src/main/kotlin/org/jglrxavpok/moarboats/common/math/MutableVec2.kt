package org.jglrxavpok.moarboats.common.math

data class MutableVec2(var x: Double, var y: Double) {
    constructor(vec: MutableVec2): this(vec.x, vec.y)

    fun scale(factor: Double) {
        x *= factor;
        y *= factor;
    }

    fun translate(dx: Double, dy: Double) {
        x += dx
        y += dy
    }

    fun lengthSq() = x*x+y*y

    fun length() = Math.sqrt(lengthSq())
}