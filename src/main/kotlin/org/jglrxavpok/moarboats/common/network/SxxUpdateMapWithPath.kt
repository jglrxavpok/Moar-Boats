package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

abstract class SxxUpdateMapWithPath: IMessage {

    constructor()

    lateinit var list: NBTTagList

    constructor(waypointList: NBTTagList) {
        this.list = waypointList
    }

    override fun fromBytes(buf: ByteBuf) {
        val tag = ByteBufUtils.readTag(buf) as NBTTagCompound
        list = tag.getTagList("list", Constants.NBT.TAG_COMPOUND)
    }

    override fun toBytes(buf: ByteBuf) {
        val tag = NBTTagCompound().apply { setTag("list", list) }
        ByteBufUtils.writeTag(buf, tag)
    }

    abstract class Handler<T: SxxUpdateMapWithPath>: IMessageHandler<T, IMessage?> {
        abstract fun updatePath(message: T, ctx: MessageContext, list: NBTTagList)

        override fun onMessage(message: T, ctx: MessageContext): IMessage? {
            val list = message.list
            updatePath(message, ctx, list)
            return null
        }
    }


}