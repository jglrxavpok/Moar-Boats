package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class S16ModuleLocations(): IMessage {

    var modules = emptyList<ResourceLocation>()

    var boatID: Int = 0

    constructor(boatID: Int, modules: List<ResourceLocation>): this() {
        this.boatID = boatID
        this.modules = modules
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        val size = buf.readInt()
        val list = mutableListOf<ResourceLocation>()
        for(i in 0 until size) {
            val location = ResourceLocation(ByteBufUtils.readUTF8String(buf))
            list.add(location)
        }
        modules = list
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        buf.writeInt(modules.size)
        for(i in 0 until modules.size) {
            ByteBufUtils.writeUTF8String(buf, modules[i].toString())
        }
    }

    object Handler: IMessageHandler<S16ModuleLocations, IMessage> {
        override fun onMessage(message: S16ModuleLocations, ctx: MessageContext): IMessage? {
            val world = Minecraft.getMinecraft().world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            boat.moduleLocations.clear()
            boat.moduleLocations.addAll(message.modules)
            return null
        }
    }
}