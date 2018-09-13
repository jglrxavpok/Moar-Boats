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

class C12AddWaypoint(): IMessage {

    var x: Int = 0
    var z: Int = 0
    var boatID: Int = 0

    constructor(blockPos: BlockPos, boatID: Int): this() {
        x = blockPos.x
        z = blockPos.z
        this.boatID = boatID
    }


    override fun fromBytes(buf: ByteBuf) {
        x = buf.readInt()
        z = buf.readInt()
        boatID = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(z)
        buf.writeInt(boatID)
    }

    object Handler: IMessageHandler<C12AddWaypoint, IMessage> {
        override fun onMessage(message: C12AddWaypoint, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            val stack = boat.getInventory(HelmModule).getStackInSlot(0)
            val item = stack.item as? ItemMap ?: error("Got click while there was no map!")
            val mapData = item.getMapData(stack, boat.worldRef)!!

            val mapScale = (1 shl mapData.scale.toInt())
            val size = mapScale*128
            HelmModule.addWaypoint(boat,
                    message.x,
                    message.z,
                    renderPos(message.x, mapData.xCenter, size),
                    renderPos(message.z, mapData.zCenter, size))
            return null
        }

        private fun renderPos(pos: Int, center: Int, size: Int): Int {
            val pixel = ((pos-center+size/2).toDouble()/size) * 128f
            return pixel.toInt()
        }

    }
}