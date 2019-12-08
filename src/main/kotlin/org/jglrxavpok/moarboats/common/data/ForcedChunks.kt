package org.jglrxavpok.moarboats.common.data

import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

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
        val dimensiontype = world.world.getDimension().type
        val serverworld = world.world.server!!.getWorld(dimensiontype)
        serverworld.forceChunk(ChunkPos.getX(position), ChunkPos.getZ(position), true)
    }

    fun forceAfterWorldLoad() {
        chunks.values.forEach {
            force(it.position)
        }
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

    fun write(tag: CompoundNBT): CompoundNBT {
        val list = ListNBT()
        val now = System.currentTimeMillis()
        chunks.forEach { (position, chunk) ->
            val chunkInfo = CompoundNBT()
            chunkInfo.putLong("chunkCompactPosition", position)
            chunkInfo.putLong("delta", now-chunk.timestamp)
            list.add(chunkInfo)
        }
        tag.put("list", list)
        return tag
    }

    fun read(tag: CompoundNBT) {
        val list = tag.getList("list", Constants.NBT.TAG_COMPOUND)
        val now = System.currentTimeMillis()
        list.forEach {
            it as CompoundNBT
            val delta = it.getLong("delta")
            val timestamp = now+delta
            val position = it.getLong("chunkCompactPosition")
            chunks[position] = ForcedChunk(position, timestamp)
        }
    }

    private fun unforce(positions: Iterable<ChunkCompactPosition>) {
        val dimensiontype = world.world.getDimension().type
        val serverworld = world.world.server!!.getWorld(dimensiontype)

        positions.forEach {
            val x = ChunkPos.getX(it)
            val z = ChunkPos.getZ(it)
            serverworld.forceChunk(x, z, false)
        }
    }
}