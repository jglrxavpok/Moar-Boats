package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemMap
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.modules.HelmModule

class C1MapClick(): IMessage {

    var pixelX: Int = 0
    var pixelY: Int = 0
    var mapAreaSize: Double = 0.0
    var button: Int = 0

    constructor(pixelX: Int, pixelY: Int, mapAreaSize: Double, mouseButton: Int): this() {
        this.pixelX = pixelX
        this.pixelY = pixelY
        this.mapAreaSize = mapAreaSize
        this.button = mouseButton
    }

    override fun fromBytes(buf: ByteBuf) {
        mapAreaSize = buf.readDouble()
        pixelX = buf.readInt()
        pixelY = buf.readInt()
        button = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeDouble(mapAreaSize)
        buf.writeInt(pixelX)
        buf.writeInt(pixelY)
        buf.writeInt(button)
    }

    object Handler: IMessageHandler<C1MapClick, IMessage> {
        override fun onMessage(message: C1MapClick, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val container = player.openContainer
            if(container !is ContainerHelmModule) {
                error("Invalid container, expected ContainerHelmModule, got $container")
            }
            val stack = container.getSlot(0).stack
            val item = stack.item
            if(item !is ItemMap) {
                error("Got click while there was no map!")
            }
            val boat = container.boat
            val mapdata = item.getMapData(stack, boat.worldRef)!!
            val helm = container.helm as HelmModule
            val mapScale = (1 shl mapdata.scale.toInt()).toFloat()
            val pixelsToMap = 128f/message.mapAreaSize

            when(message.button) {
                0 -> helm.addWaypoint(boat,
                        pixel2map(message.pixelX, mapdata.xCenter, message.mapAreaSize, mapScale),
                        pixel2map(message.pixelY, mapdata.zCenter, message.mapAreaSize, mapScale),
                        (message.pixelX * pixelsToMap).toInt(), (message.pixelY * pixelsToMap).toInt())

                1 -> helm.removeLastWaypoint(boat)
            }


            return null
        }

        private fun pixel2map(pixel: Int, center: Int, mapAreaSize: Double, mapScale: Float): Int {
            val pixelsToMap = 128f/mapAreaSize
            return Math.floor((center / mapScale + (pixel - mapAreaSize /2) * pixelsToMap) * mapScale).toInt()
        }
    }
}