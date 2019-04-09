package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import kotlin.reflect.KClass

abstract class CChangeLoopingStateBase(): IMessage {

    var loopingOption: LoopingOptions = LoopingOptions.NoLoop

    constructor(loopingOption: LoopingOptions): this() {
        this.loopingOption = loopingOption
    }

    override fun fromBytes(buf: ByteBuf) {
        loopingOption = LoopingOptions.values()[buf.readInt().coerceIn(LoopingOptions.values().indices)]
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(loopingOption.ordinal)
    }

}