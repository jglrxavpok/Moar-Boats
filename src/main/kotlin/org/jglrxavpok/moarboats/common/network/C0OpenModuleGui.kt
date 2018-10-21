package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class C0OpenModuleGui(): IMessage {
    var boatID: Int = 0
    lateinit var moduleID: ResourceLocation

    constructor(boatID: Int, id: ResourceLocation): this() {
        this.boatID = boatID
        this.moduleID = id
    }

    override fun fromBytes(buf: ByteBuf) {
        moduleID = ResourceLocation(ByteBufUtils.readUTF8String(buf))
        boatID = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, moduleID.toString())
        buf.writeInt(boatID)
    }

    object Handler: MBMessageHandler<C0OpenModuleGui, IMessage> {
        override val packetClass = C0OpenModuleGui::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: C0OpenModuleGui, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val boat = player.world.getEntityByID(message.boatID) as? ModularBoatEntity
            if(boat == null) {
                MoarBoats.logger.debug("$player tried to open boat menu while the boat with ID ${message.boatID} is not loaded (or doesn't exist?)")
                return null
            }
            val moduleIndex = boat.modules.indexOfFirst { it.id == message.moduleID }
            player.openGui(MoarBoats, MoarBoatsGuiHandler.ModulesGui, player.world, message.boatID, moduleIndex, 0)
            return null
        }
    }


}