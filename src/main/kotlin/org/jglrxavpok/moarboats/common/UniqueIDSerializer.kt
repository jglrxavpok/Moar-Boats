package org.jglrxavpok.moarboats.common

import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.IDataSerializer
import java.util.*

object UniqueIDSerializer: IDataSerializer<UUID> {
    override fun write(buf: PacketBuffer, value: UUID) {
        buf.writeUniqueId(value)
    }

    override fun copyValue(value: UUID) = value

    override fun createKey(id: Int) = DataParameter(id, this)

    override fun read(buf: PacketBuffer) = buf.readUniqueId()
}