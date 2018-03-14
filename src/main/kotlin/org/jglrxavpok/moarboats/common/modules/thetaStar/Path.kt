package org.jglrxavpok.moarboats.common.modules.thetaStar

import org.jglrxavpok.moarboats.common.modules.SurroundingsMatrix
import java.util.*

data class PathNode(val x: Int, val z: Int, var count: Int, var heuristic: Int) {

    operator fun compareTo(other: PathNode): Int {
        if(heuristic < other.heuristic)
            return 1
        if(heuristic == other.heuristic)
            return 0
        return -1
    }
}

class Path {

    val nodes = mutableListOf<PathNode>()
    val size get()= nodes.size

    fun isEmpty() = nodes.isEmpty()

    companion object {
        fun computePath(inside: SurroundingsMatrix, towardsIndex: Int): Path {
            val goalX = inside.index2posX(towardsIndex)
            val goalZ = inside.index2posZ(towardsIndex)
            val requiredState = inside[0, 0] ?: return Path()
            val path = Path()
            // TODO: http://idm-lab.org/bib/abstracts/papers/aaai10b.pdf
            return path
        }
    }
}