package org.jglrxavpok.moarboats.common.data

import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World

private typealias ChunkCompactPosition = Long

/**
 * Responsible for holding a set of forced-loaded chunks. Ensures chunk forced through this object are unforced 10 seconds after
 */
class ForcedChunks(val world: World) {

    /**
     * A forced chunk with its last-updated timestamp
     */
    private data class ForcedChunk(val position: ChunkCompactPosition, var timestamp: Long = System.currentTimeMillis()) {
        fun resetTimestamp() {
            timestamp = System.currentTimeMillis()
        }
    }

    private val chunks = mutableMapOf<ChunkCompactPosition, ForcedChunk>()

    /**
     * Force a chunk position to be loaded.
     * ChunkPos#getChunkPos
     */
    fun force(position: ChunkCompactPosition) {
        val chunk = chunks.getOrPut(position) { ForcedChunk(position) }
        chunk.resetTimestamp()
        world.setChunkForced(ChunkPos.getX(position), ChunkPos.getZ(position), true)
    }

    /**
     * Unforces chunks that have been last forced 10+ seconds ago
     */
    fun update() {
        val deletionQueue by lazy { mutableListOf<ChunkCompactPosition>() }
        val now = System.currentTimeMillis()
        chunks.forEach { (position, forcedChunk) ->
            val delta = now-forcedChunk.timestamp
            if(delta > 10*1000) {
                deletionQueue += position
            }
        }

        unforce(deletionQueue)
        deletionQueue.clear()
    }

    /**
     * Unforces all chunks inside this object
     */
    fun removeAll() {
        unforce(chunks.keys)
        chunks.clear()
    }

    private fun unforce(positions: Iterable<ChunkCompactPosition>) {
        positions.forEach {
            val x = ChunkPos.getX(it)
            val z = ChunkPos.getZ(it)
            world.setChunkForced(x, z, false)
        }
    }
}