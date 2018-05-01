package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemMap
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModuleRegistry

class C2MapRequest(): IMessage {

    var mapName: String = ""
    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    constructor(name: String, boatID: Int, moduleLocation: ResourceLocation): this() {
        this.mapName = name
        this.boatID = boatID
        this.moduleLocation = moduleLocation
    }

    override fun fromBytes(buf: ByteBuf) {
        mapName = ByteBufUtils.readUTF8String(buf)
        boatID = buf.readInt()
        moduleLocation = ResourceLocation(ByteBufUtils.readUTF8String(buf))
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, mapName)
        buf.writeInt(boatID)
        ByteBufUtils.writeUTF8String(buf, moduleLocation.toString())
    }

    object Handler: IMessageHandler<C2MapRequest, S3MapAnswer> {
        override fun onMessage(message: C2MapRequest, ctx: MessageContext): S3MapAnswer? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            val stack = boat.getInventory(module).getStackInSlot(0)
            val item = stack.item as? ItemMap ?: return null // Got request while there was no map!
            val mapName = message.mapName
            val mapdata = item.getMapData(stack, boat.worldRef)!!
            val packet = S3MapAnswer(mapName)
            mapdata.writeToNBT(packet.mapData)
            return packet
        }
    }
}