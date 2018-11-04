package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CChangeLoopingState(): IMessage {

    var boatID: Int = 0
    var loops: Boolean = false

    constructor(loops: Boolean, boatID: Int): this() {
        this.boatID = boatID
        this.loops = loops
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        loops = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        buf.writeBoolean(loops)
    }

    object Handler: MBMessageHandler<CChangeLoopingState, IMessage> {
        override val packetClass = CChangeLoopingState::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CChangeLoopingState, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            HelmModule.loopingProperty[boat] = message.loops
            return null
        }
    }
}