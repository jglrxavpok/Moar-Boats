package org.jglrxavpok.moarboats.common

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.api.Link
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

object BoatLinksSerializer : EntityDataSerializer<MutableMap<Cleat, Link>> {
    override fun copy(value: MutableMap<Cleat, Link>): MutableMap<Cleat, Link> {
        val copy = mutableMapOf<Cleat, Link>()
        copy.putAll(value)
        return copy
    }

    override fun read(buf: FriendlyByteBuf): MutableMap<Cleat, Link> {
        val count = buf.readInt()
        val result = mutableMapOf<Cleat, Link>()
        for(i in 0 until count) {
            val cleatType = buf.readResourceLocation()
            val cleat = Cleats.Registry.get().getValue(cleatType) ?: error("No cleat with type $cleatType")
            val link = Link(cleat)
            link.read(buf)
            result += cleat to link
        }
        return result
    }

    override fun createAccessor(id: Int): EntityDataAccessor<MutableMap<Cleat, Link>> {
        return EntityDataAccessor(id, this)
    }

    override fun write(buf: FriendlyByteBuf, value: MutableMap<Cleat, Link>) {
        buf.writeInt(value.size)
        for((cleat, link) in value) {
            buf.writeResourceLocation(Cleats.Registry.get().getKey(cleat))
            link.write(buf)
        }
    }
}