package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import kotlin.reflect.KClass

class C0OpenModuleGui(): IMessage {
    var boatID: Int = 0
    var moduleIndex: Int = 0

    constructor(boatID: Int, index: Int): this() {
        this.boatID = boatID
        this.moduleIndex = index
    }

    override fun fromBytes(buf: ByteBuf) {
        moduleIndex = buf.readInt()
        boatID = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(moduleIndex)
        buf.writeInt(boatID)
    }

    object Handler: MBMessageHandler<C0OpenModuleGui, IMessage> {
        override val packetClass = C0OpenModuleGui::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: C0OpenModuleGui, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            player.openGui(MoarBoats, MoarBoatsGuiHandler.ModulesGui, player.world, message.boatID, message.moduleIndex, 0)
            return null
        }
    }


}