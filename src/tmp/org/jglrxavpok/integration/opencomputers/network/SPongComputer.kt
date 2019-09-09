package org.jglrxavpok.moarboats.integration.opencomputers.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.network.MBMessageHandler
import org.jglrxavpok.moarboats.integration.opencomputers.client.GuiComputerModule

class SPongComputer(): IMessage {

    private var boatID = 0
    private var running = false

    constructor(boatID: Int, running: Boolean): this() {
        this.boatID = boatID
        this.running = running
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        running = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        buf.writeBoolean(running)
    }

    object Handler: MBMessageHandler<SPongComputer, IMessage?> {
        override val packetClass = SPongComputer::class
        override val receiverSide = Side.CLIENT

        override fun onMessage(message: SPongComputer, ctx: MessageContext): IMessage? {
            with(message) {
                val screen = Minecraft.getMinecraft().screen
                if(screen is GuiComputerModule) {
                    if(screen.boat.id == boatID) {
                        screen.pong(running)
                    }
                }
            }
            return null
        }
    }
}