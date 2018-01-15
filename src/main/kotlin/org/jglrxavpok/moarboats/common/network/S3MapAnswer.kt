package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.MapData
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class S3MapAnswer(): IMessage {

    var mapName = ""
    var mapData = NBTTagCompound()

    constructor(name: String): this() {
        this.mapName = name
    }

    override fun fromBytes(buf: ByteBuf) {
        mapName = ByteBufUtils.readUTF8String(buf)
        mapData = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, mapName)
        ByteBufUtils.writeTag(buf, mapData)
    }

    object Handler: IMessageHandler<S3MapAnswer, IMessage> {
        override fun onMessage(message: S3MapAnswer, ctx: MessageContext): IMessage? {
            val mapID = message.mapName
            val data = MapData(mapID)
            data.readFromNBT(message.mapData)
            Minecraft.getMinecraft().world.setData(mapID, data)
            return null
        }
    }
}