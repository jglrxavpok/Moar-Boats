package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class CRemoveModule(): IMessage {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    constructor(boatID: Int, moduleLocation: ResourceLocation): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        moduleLocation = ResourceLocation(ByteBufUtils.readUTF8String(buf))
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        ByteBufUtils.writeUTF8String(buf, moduleLocation.toString())
    }

    object Handler: MBMessageHandler<CRemoveModule, SMapAnswer> {
        override val packetClass = CRemoveModule::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CRemoveModule, ctx: MessageContext): SMapAnswer? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            boat.removeModule(moduleLocation)
            player.closeScreen()
            return null
        }
    }
}