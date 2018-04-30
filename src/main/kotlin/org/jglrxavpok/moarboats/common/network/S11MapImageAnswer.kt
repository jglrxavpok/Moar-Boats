package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.world.storage.MapData
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.common.data.MapImageStripe
import org.jglrxavpok.moarboats.common.modules.HelmModule.StripeLength

class S11MapImageAnswer(): IMessage {

    var mapName = ""
    var stripeIndex = 0
    var textureStripe = intArrayOf()

    constructor(name: String, stripeIndex: Int, textureStripe: IntArray): this() {
        this.mapName = name
        this.stripeIndex = stripeIndex
        this.textureStripe = textureStripe
    }

    override fun fromBytes(buf: ByteBuf) {
        mapName = ByteBufUtils.readUTF8String(buf)
        stripeIndex = buf.readInt()
        val stripePixelCount = buf.readInt()
        textureStripe = IntArray(stripePixelCount)
        for(i in 0 until textureStripe.size) {
            textureStripe[i] = buf.readInt()
        }
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, mapName)
        buf.writeInt(stripeIndex)
        buf.writeInt(textureStripe.size)
        for(i in 0 until textureStripe.size) {
            buf.writeInt(textureStripe[i])
        }
    }

    object Handler: IMessageHandler<S11MapImageAnswer, IMessage> {
        override fun onMessage(message: S11MapImageAnswer, ctx: MessageContext): IMessage? {
            val mapID = message.mapName
            val id = "moarboats:map_preview/$mapID/${message.stripeIndex}"
            val data = MapImageStripe(id, message.stripeIndex, message.textureStripe)
            Minecraft.getMinecraft().world.setData(id, data)
            return null
        }
    }
}