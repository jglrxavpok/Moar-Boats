package org.jglrxavpok.moarboats.integration.opencomputers.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.network.MBMessageHandler
import org.jglrxavpok.moarboats.integration.opencomputers.OpenComputerPlugin

class SSyncMachineData(): IMessage {

    private var boatID = 0
    private lateinit var data: NBTTagCompound

    constructor(boatID: Int, data: NBTTagCompound): this() {
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
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
                OpenComputerPlugin.getHost(boat)?.let {
                    it.processInitialData(data)
                }
            }
            return null
        }
    }
}