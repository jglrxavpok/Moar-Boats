package org.jglrxavpok.moarboats.integration.opencomputers.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.network.MBMessageHandler
import org.jglrxavpok.moarboats.integration.opencomputers.OpenComputersPlugin

class CTurnOnOffComputer(): IMessage {

    private var boatID = 0
    private var turnOn = false

    constructor(boatID: Int, turnOn: Boolean): this() {
        this.boatID = boatID
        this.turnOn = turnOn
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        turnOn = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        buf.writeBoolean(turnOn)
    }

    object Handler: MBMessageHandler<CTurnOnOffComputer, IMessage?> {
        override val packetClass = CTurnOnOffComputer::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CTurnOnOffComputer, ctx: MessageContext): IMessage? {
            with(message) {
                val world = ctx.serverHandler.player.world
                val boat = world.getEntity(message.boatID) as? ModularBoatEntity ?: return null
                OpenComputersPlugin.getHost(boat)?.let {
                    if(turnOn) {
                        it.turnOn()
                    } else {
                        it.turnOff()
                    }
                }
            }
            return null
        }
    }
}