package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemMap
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class C14ChangeLoopingState(): IMessage {

    var boatID: Int = 0
    var loops: Boolean = false

    constructor(loops: Boolean, boatID: Int): this() {
        this.boatID = boatID
        this.loops = loops
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        loops = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        buf.writeBoolean(loops)
    }

    object Handler: MBMessageHandler<C14ChangeLoopingState, IMessage> {
        override val packetClass = C14ChangeLoopingState::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: C14ChangeLoopingState, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            HelmModule.loopingProperty[boat] = message.loops
            return null
        }
    }
}