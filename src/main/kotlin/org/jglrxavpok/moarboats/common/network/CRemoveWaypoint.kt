package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CRemoveWaypoint(): IMessage {

    var waypointIndex: Int = 0
    var boatID: Int = 0

    constructor(waypointIndex: Int, boatID: Int): this() {
        this.boatID = boatID
        this.waypointIndex = waypointIndex
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        waypointIndex = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        buf.writeInt(waypointIndex)
    }

    object Handler: MBMessageHandler<CRemoveWaypoint, IMessage> {
        override val packetClass = CRemoveWaypoint::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CRemoveWaypoint, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            HelmModule.removeWaypoint(boat, message.waypointIndex)
            return null
        }
    }
}