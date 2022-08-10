package org.jglrxavpok.moarboats.common

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.api.Link
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import java.util.concurrent.ConcurrentHashMap

object BoatLinksSerializer : EntityDataSerializer<ConcurrentHashMap<Cleat, Link>> {
    override fun copy(value: ConcurrentHashMap<Cleat, Link>): ConcurrentHashMap<Cleat, Link> {
        return ConcurrentHashMap<Cleat, Link>(value)
    }

    override fun read(buf: FriendlyByteBuf): ConcurrentHashMap<Cleat, Link> {
        val count = buf.readInt()
        val result = ConcurrentHashMap<Cleat, Link>()
        for(i in 0 until count) {
            val cleatType = buf.readResourceLocation()
            val cleat = Cleats.Registry.get().getValue(cleatType) ?: error("No cleat with type $cleatType")
            val link = Link(cleat)
            link.read(buf)
            result += cleat to link
        }
        return result
    }

    override fun createAccessor(id: Int): EntityDataAccessor<ConcurrentHashMap<Cleat, Link>> {
        return EntityDataAccessor(id, this)
    }

    override fun write(buf: FriendlyByteBuf, value: ConcurrentHashMap<Cleat, Link>) {
        buf.writeInt(value.size)
        for((cleat, link) in value) {
            buf.writeResourceLocation(Cleats.Registry.get().getKey(cleat))
            link.write(buf)
        }
    }
}