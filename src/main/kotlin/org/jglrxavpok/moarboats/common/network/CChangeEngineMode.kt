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

class CChangeEngineMode(): IMessage {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    private var locked: Boolean = false

    constructor(boatID: Int, moduleLocation: ResourceLocation, locked: Boolean): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
        this.locked = locked
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        locked = buf.readBoolean()
        moduleLocation = ResourceLocation(ByteBufUtils.readUTF8String(buf))
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        buf.writeBoolean(locked)
        ByteBufUtils.writeUTF8String(buf, moduleLocation.toString())
    }

    object Handler: MBMessageHandler<CChangeEngineMode, IMessage?> {
        override val packetClass = CChangeEngineMode::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CChangeEngineMode, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            module as BaseEngineModule
            module.setStationary(boat, message.locked)
            return null
        }
    }

}