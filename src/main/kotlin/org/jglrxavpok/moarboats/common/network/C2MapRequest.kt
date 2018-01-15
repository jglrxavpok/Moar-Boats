package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemMap
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule

class C2MapRequest(): IMessage {

    var mapName: String = ""
    // TODO: add boatID + moduleID

    constructor(name: String): this() {
        this.mapName = name
    }

    override fun fromBytes(buf: ByteBuf) {
        mapName = ByteBufUtils.readUTF8String(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, mapName)
    }

    object Handler: IMessageHandler<C2MapRequest, S3MapAnswer> {
        override fun onMessage(message: C2MapRequest, ctx: MessageContext): S3MapAnswer? {
            val player = ctx.serverHandler.player
            val container = player.openContainer
            if(container !is ContainerHelmModule) {
                error("Invalid container, expected ContainerHelmModule, got $container")
            }
            val stack = container.getSlot(0).stack
            val item = stack.item
            if(item !is ItemMap) {
                error("Got request while there was no map!")
            }
            val boat = container.boat
            val mapName = message.mapName
            val mapdata = item.getMapData(stack, boat.worldRef)!!
            val packet = S3MapAnswer(mapName)
            mapdata.writeToNBT(packet.mapData)
            return packet
        }
    }
}