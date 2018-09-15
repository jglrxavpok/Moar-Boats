package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.MapStorage
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket

class S21SetGoldenItinerary(): IMessage {

    lateinit var data: ItemGoldenTicket.WaypointData

    constructor(data: ItemGoldenTicket.WaypointData): this() {
        this.data = data
    }

    override fun fromBytes(buf: ByteBuf) {
        val uuid = ByteBufUtils.readUTF8String(buf)
        val compound = ByteBufUtils.readTag(buf)!!
        data = ItemGoldenTicket.WaypointData(uuid)
        data.backingList = compound.getTagList("list", Constants.NBT.TAG_COMPOUND)
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, data.uuid)
        val compound = NBTTagCompound()
        compound.setTag("list", data.backingList)
        ByteBufUtils.writeTag(buf, compound)
    }

    object Handler: IMessageHandler<S21SetGoldenItinerary, IMessage?> {
        override fun onMessage(message: S21SetGoldenItinerary, ctx: MessageContext): IMessage? {
            val mapStorage: MapStorage = MoarBoats.getLocalMapStorage()
            mapStorage.setData(message.data.uuid, message.data)
            message.data.isDirty = true
            return null
        }
    }
}