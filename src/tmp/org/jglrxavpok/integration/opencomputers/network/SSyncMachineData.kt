package org.jglrxavpok.moarboats.integration.opencomputers.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.network.MBMessageHandler
import org.jglrxavpok.moarboats.integration.opencomputers.OpenComputersPlugin

class SSyncMachineData(): IMessage {

    private var boatID = 0
    private lateinit var data: CompoundNBT

    constructor(boatID: Int, data: CompoundNBT): this() {
        this.boatID = boatID
        this.data = data
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        data = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        ByteBufUtils.writeTag(buf, data)
    }

    object Handler: MBMessageHandler<SSyncMachineData, IMessage?> {
        override val packetClass = SSyncMachineData::class
        override val receiverSide = Side.CLIENT

        override fun onMessage(message: SSyncMachineData, ctx: MessageContext): IMessage? {
            with(message) {
                val world = Minecraft.getMinecraft().world
                val boat = world.getEntity(message.boatID) as? ModularBoatEntity ?: return null
                OpenComputersPlugin.getHost(boat)?.processInitialData(data)
            }
            return null
        }
    }
}