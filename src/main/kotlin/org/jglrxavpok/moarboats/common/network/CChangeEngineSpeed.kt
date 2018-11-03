package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.BaseEngineModule

class CChangeEngineSpeed(): IMessage {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")
    var speed: Float = 0f

    constructor(boatID: Int, moduleLocation: ResourceLocation, speed: Float): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
        this.speed = speed
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        moduleLocation = ResourceLocation(ByteBufUtils.readUTF8String(buf))
        speed = buf.readFloat()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        ByteBufUtils.writeUTF8String(buf, moduleLocation.toString())
        buf.writeFloat(speed)
    }

    object Handler: MBMessageHandler<CChangeEngineSpeed, IMessage?> {
        override val packetClass = CChangeEngineSpeed::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CChangeEngineSpeed, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            module as BaseEngineModule
            module.changeSpeed(boat, message.speed)
            return null
        }
    }

}