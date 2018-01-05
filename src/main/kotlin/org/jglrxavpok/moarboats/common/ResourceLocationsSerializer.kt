package org.jglrxavpok.moarboats.common

import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializer
import net.minecraft.util.ResourceLocation

object ResourceLocationsSerializer : DataSerializer<MutableList<ResourceLocation>> {
    override fun copyValue(value: MutableList<ResourceLocation>): MutableList<ResourceLocation> {
        val copy = mutableListOf<ResourceLocation>()
        copy.addAll(value)
        return copy
    }

    override fun read(buf: PacketBuffer): MutableList<ResourceLocation> {
        val count = buf.readInt()
        val result = mutableListOf<ResourceLocation>()
        for(i in 0 until count) {
            val location = buf.readResourceLocation()
            result += location
        }
        return result
    }

    override fun createKey(id: Int): DataParameter<MutableList<ResourceLocation>> {
        return DataParameter<MutableList<ResourceLocation>>(id, this)
    }

    override fun write(buf: PacketBuffer, value: MutableList<ResourceLocation>) {
        buf.writeInt(value.size)
        for(location in value) {
            buf.writeResourceLocation(location)
        }
    }
}