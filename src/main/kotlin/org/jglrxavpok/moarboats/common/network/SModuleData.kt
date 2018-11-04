package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class SModuleData(): IMessage {

    var data = NBTTagCompound()

    var boatID: Int = 0

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

    object Handler: MBMessageHandler<SModuleData, IMessage> {
        override val packetClass = SModuleData::class
        override val receiverSide = Side.CLIENT

        override fun onMessage(message: SModuleData, ctx: MessageContext): IMessage? {
            val world = Minecraft.getMinecraft().world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            boat.moduleData = message.data
            return null
        }
    }
}