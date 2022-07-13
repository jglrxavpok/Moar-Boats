package org.jglrxavpok.moarboats.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraftforge.common.world.ForgeChunkManager
import org.jglrxavpok.moarboats.MoarBoats

private typealias ChunkCompactPosition = Long

/**
 * Responsible for holding a set of forced-loaded chunks. Ensures chunk forced through this object are unforced 10 seconds after
 */
class ForcedChunks(val world: Level, val owner: Entity) {

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
        val dimensiontype = world.dimension()
        val ServerLevel = world.server!!.getLevel(dimensiontype) ?: error("Server world is null?? Dimension is $dimensiontype")
        val chunkX = ChunkPos.getX(position)
        val chunkZ = ChunkPos.getZ(position)
        ForgeChunkManager.forceChunk(ServerLevel, MoarBoats.ModID, owner, chunkX, chunkZ, true, true)
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

    fun write(tag: CompoundTag): CompoundTag {
        val list = ListTag()
        val now = System.currentTimeMillis()
        chunks.forEach { (position, chunk) ->
            val chunkInfo = CompoundTag()
            chunkInfo.putLong("chunkCompactPosition", position)
            chunkInfo.putLong("delta", now-chunk.timestamp)
            list.add(chunkInfo)
        }
        tag.put("list", list)
        return tag
    }

    fun read(tag: CompoundTag) {
        val list = tag.getList("list", Tag.TAG_COMPOUND.toInt())
        val now = System.currentTimeMillis()
        list.forEach {
            it as CompoundTag
            val delta = it.getLong("delta")
            val timestamp = now+delta
            val position = it.getLong("chunkCompactPosition")
            chunks[position] = ForcedChunk(position, timestamp)
        }
    }

    private fun unforce(positions: Iterable<ChunkCompactPosition>) {
        val dimensiontype = world.dimension()
        val ServerLevel = world.server!!.getLevel(dimensiontype) ?: error("Server world is null?? Dimension is $dimensiontype")

        positions.forEach {
            val x = ChunkPos.getX(it)
            val z = ChunkPos.getZ(it)

            ForgeChunkManager.forceChunk(ServerLevel, MoarBoats.ModID, owner, x, z, false, true)
        }
    }
}