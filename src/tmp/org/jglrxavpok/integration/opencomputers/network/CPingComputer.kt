package org.jglrxavpok.moarboats.integration.opencomputers.network

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.network.MBMessageHandler
import org.jglrxavpok.moarboats.integration.opencomputers.OpenComputersPlugin

class CPingComputer(): IMessage {

    private var boatID = 0

    constructor(boatID: Int): this() {
        this.boatID = boatID
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
    }

    object Handler: MBMessageHandler<CPingComputer, IMessage?> {
        override val packetClass = CPingComputer::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CPingComputer, ctx: MessageContext): IMessage? {
            with(message) {
                val world = ctx.serverHandler.player.world
                val boat = world.getEntityByID(boatID) as? ModularBoatEntity ?: return null
                OpenComputersPlugin.getHost(boat)?.let {
                    return SPongComputer(boatID, it.machine.isRunning)
                }
            }
            return null
        }
    }
}