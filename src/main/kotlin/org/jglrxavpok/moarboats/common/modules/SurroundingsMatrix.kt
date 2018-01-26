package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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

    private fun index2posX(index: Int): Int {
        val matrixX = index % size
        if(matrixX % 2 == 0)
            return matrixX/2
        return -(matrixX+1)/2
    }

    private fun index2posZ(index: Int): Int {
        val matrixZ = index / size
        if(matrixZ % 2 == 0)
            return matrixZ/2
        return -(matrixZ+1)/2
    }

    /**
     * Positive or null 'x' values are mapped to even numbers, negative to odd numbers; same for z
     */
    private fun pos2index(x: Int, z: Int): Int {
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
}