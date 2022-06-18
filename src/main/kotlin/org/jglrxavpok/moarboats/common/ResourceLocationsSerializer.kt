package org.jglrxavpok.moarboats.common

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.resources.ResourceLocation

object ResourceLocationsSerializer : EntityDataSerializer<MutableList<ResourceLocation>> {
    override fun copy(value: MutableList<ResourceLocation>): MutableList<ResourceLocation> {
        val copy = mutableListOf<ResourceLocation>()
        copy.addAll(value)
        return copy
    }

    override fun read(buf: FriendlyByteBuf): MutableList<ResourceLocation> {
        val count = buf.readInt()
        val result = mutableListOf<ResourceLocation>()
        for(i in 0 until count) {
            val location = buf.readResourceLocation()
            result += location
        }
        return result
    }

    override fun createAccessor(id: Int): EntityDataAccessor<MutableList<ResourceLocation>> {
        return EntityDataAccessor<MutableList<ResourceLocation>>(id, this)
    }

    override fun write(buf: FriendlyByteBuf, value: MutableList<ResourceLocation>) {
        buf.writeInt(value.size)
        for(location in value) {
            buf.writeResourceLocation(location)
        }
    }
}