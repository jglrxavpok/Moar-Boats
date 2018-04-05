package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.lwjgl.util.vector.Vector2f

class SurroundingsMatrix(val size: Int) {

    private val halfSize = size/2

    private val internalMatrix = Array<IBlockState?>(size*size) {
        null
    }

    fun compute(world: World, centerX: Double, centerY: Double, centerZ: Double): SurroundingsMatrix {
        val pos = BlockPos.PooledMutableBlockPos.retain()
        for(xOffset in -halfSize until halfSize) {
            for(zOffset in -halfSize until halfSize) {
                val worldX = centerX + xOffset
                val worldY = centerY
                val worldZ = centerZ + zOffset
                pos.setPos(worldX, worldY, worldZ)
                val blockState = world.getBlockState(pos)
                internalMatrix[pos2index(xOffset, zOffset)] = blockState
            }
        }
        pos.release()
        return this
    }

    fun forEach(action: (Int, Int, IBlockState?) -> Unit) {
        for(xOffset in -halfSize until halfSize) {
            for (zOffset in -halfSize until halfSize) {
                val index = pos2index(xOffset, zOffset)
                val state = internalMatrix[index]
                action(xOffset, zOffset, state)
            }
        }
    }

    /**
     * Removes (sets to null) all block states not connected to the center of this matrix (using the same blockstate)
     * Returns 'this' for chaining
     */
    fun removeNotConnectedToCenter(): SurroundingsMatrix {
        val andMatrix = Array(size*size) { false }
        val queue = mutableListOf<Int>()
        queue.add(0) // center
        val done = hashSetOf<Int>()
        val requiredState: IBlockState? = internalMatrix[0]
        while(!queue.isEmpty()) { // flood fill
            val index = queue.removeAt(0)
            if(index in done)
                continue
            done.add(index)
            val foundState = internalMatrix[index]
            if(foundState != requiredState)
                continue

            andMatrix[index] = true
            // correct state, add neighbors
            val x = index2posX(index)
            val z = index2posZ(index)
            for(j in -1..1) {
                for (i in -1..1) {
                    if(i != 0 || j != 0) {
                        val neighborIndex = pos2index(x + i, z + j)
                        if(neighborIndex < 0 || neighborIndex >= size*size)
                            continue
                        queue.add(neighborIndex)
                    }
                }
            }
        }
        return and(andMatrix)
    }

    fun computeGradient(): Array<Vector2f> {
        val distanceMap = IntArray(internalMatrix.size)
        val requiredState = this[0, 0]
        forEach { x, z, state ->
            val index = pos2index(x, z)
            if(state != requiredState) {
                distanceMap[index] = 0
            } else {
                val dist = maximumDistance(x, z, requiredState)
                distanceMap[index] = -dist
            }
        }
        val gradient = Array(internalMatrix.size) { i-> Vector2f() }
        forEach { x, z, state ->

            fun distance(dx: Int, dz: Int): Int {
                val newX = x + dx
                val newZ = z + dz
                if(newX < halfSize && newX > -halfSize
                        && newZ < halfSize && newZ > -halfSize)
                    return distanceMap[pos2index(newX, newZ)]
                return 0
            }

            // From a Sobel filter implementation: compute gradient from distance to shore
            var horizontalContribution = 0
            horizontalContribution += distance(-1, -1) * -1
            horizontalContribution += distance(-1, 0) * -2
            horizontalContribution += distance(-1, +1) * -1
            horizontalContribution += distance(+1, -1) * 1
            horizontalContribution += distance(+1, 0) * 2
            horizontalContribution += distance(+1, +1) * 1

            var verticalContribution = 0

            verticalContribution += distance(-1, -1) * -1
            verticalContribution += distance(0, -1) * -2
            verticalContribution += distance(+1, -1) * -1
            verticalContribution += distance(-1, +1) * 1
            verticalContribution += distance(0, +1) * 2
            verticalContribution += distance(+1, +1) * 1
            gradient[pos2index(x, z)] = Vector2f(horizontalContribution.toFloat(), verticalContribution.toFloat())

        }
        return gradient
    }

    private tailrec fun maximumDistance(x: Int, z: Int, state: IBlockState?, radius: Int = 0): Int = when {
        circleFit(state, radius, x, z) -> maximumDistance(x, z, state, radius+1)
        else -> radius
    }

    private fun circleFit(state: IBlockState?, radius: Int, x0: Int, z0: Int): Boolean {
        // adapted from https://en.wikipedia.org/wiki/Midpoint_circle_algorithm
        fun foundIntersection(dx: Int, dz: Int): Boolean {
            return this[x0+dx, z0+dz] != state
        }
        var x = radius-1
        var z = 0
        var dx = 1
        var dz = 1
        var err = dx - (radius shl 1)
        while(x >= z) {
            if(foundIntersection(x, z))
                return false
            if(foundIntersection(x, -z))
                return false
            if(foundIntersection(-x, z))
                return false
            if(foundIntersection(-x, -z))
                return false

            if(foundIntersection(z, x))
                return false
            if(foundIntersection(-z, x))
                return false
            if(foundIntersection(z, -x))
                return false
            if(foundIntersection(-z, -x))
                return false

            if(err <= 0) {
                z++
                err += dz
                dz += 2
            }

            if(err > 0) {
                x--
                dx += 2
                err += dx - (radius shl 1)
            }
        }
        return true
    }

    fun and(matrix: Array<Boolean>): SurroundingsMatrix {
        if(matrix.size != internalMatrix.size)
            error("Wrong matrix size, expected ${internalMatrix.size}, got ${matrix.size}")
        for (j in 0 until size) {
            for (i in 0 until size) {
                if( ! matrix[i+j*size]) {
                    internalMatrix[i+j*size] = null
                }
            }
        }
        return this
    }

    fun index2posX(index: Int): Int {
        val matrixX = index % size
        if(matrixX % 2 == 0)
            return matrixX/2
        return -(matrixX+1)/2
    }

    fun index2posZ(index: Int): Int {
        val matrixZ = index / size
        if(matrixZ % 2 == 0)
            return matrixZ/2
        return -(matrixZ+1)/2
    }

    /**
     * Positive or null 'x' values are mapped to even numbers, negative to odd numbers; same for z
     */
    fun pos2index(x: Int, z: Int): Int {
        val matrixX = if(x >= 0) {
            x*2
        } else {
            (-x)*2-1
        }
        val matrixZ = if(z >= 0) {
            z*2
        } else {
            (-z)*2-1
        }
        return matrixX + matrixZ*size
    }

    operator fun get(x: Int, z: Int): IBlockState? {
        val index = pos2index(x, z)
        if(index < 0 || index >= size*size)
            return null
        return internalMatrix[index]
    }
}