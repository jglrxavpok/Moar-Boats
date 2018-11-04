package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import kotlin.reflect.KClass

abstract class CChangeLoopingStateBase(): IMessage {

    var loops: Boolean = false

    constructor(loops: Boolean): this() {
        this.loops = loops
    }

    override fun fromBytes(buf: ByteBuf) {
        loops = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeBoolean(loops)
    }

}