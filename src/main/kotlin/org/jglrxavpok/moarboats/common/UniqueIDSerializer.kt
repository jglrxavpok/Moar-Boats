package org.jglrxavpok.moarboats.common

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import java.util.*

object UniqueIDSerializer: EntityDataSerializer<UUID> {
    override fun write(buf: FriendlyByteBuf, value: UUID) {
        buf.writeUUID(value)
    }

    override fun copy(value: UUID) = value

    override fun createAccessor(id: Int) = EntityDataAccessor(id, this)

    override fun read(buf: FriendlyByteBuf) = buf.readUUID()
}