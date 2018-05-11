package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemMap
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class C13RemoveWaypoint(): IMessage {

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

    object Handler: IMessageHandler<C13RemoveWaypoint, IMessage> {
        override fun onMessage(message: C13RemoveWaypoint, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            HelmModule.removeWaypoint(boat, message.waypointIndex)
            return null
        }
    }
}