package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemMap
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class C12AddWaypoint(): IMessage {

    var x: Int = 0
    var z: Int = 0
    var boatID: Int = 0
    var boost: Double? = null

    constructor(blockPos: BlockPos, boatID: Int, boost: Double?): this() {
        x = blockPos.x
        z = blockPos.z
        this.boatID = boatID
        this.boost = boost
    }


    override fun fromBytes(buf: ByteBuf) {
        x = buf.readInt()
        z = buf.readInt()
        boatID = buf.readInt()
        boost = if(buf.readBoolean())
            buf.readDouble()
        else
            null
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(z)
        buf.writeInt(boatID)
        buf.writeBoolean(boost != null)
        if(boost != null)
            buf.writeDouble(boost!!)
    }

    object Handler: MBMessageHandler<C12AddWaypoint, IMessage> {
        override val packetClass = C12AddWaypoint::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: C12AddWaypoint, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            HelmModule.addWaypoint(boat,
                    message.x,
                    message.z,
                    message.boost)
            return null
        }


    }
}