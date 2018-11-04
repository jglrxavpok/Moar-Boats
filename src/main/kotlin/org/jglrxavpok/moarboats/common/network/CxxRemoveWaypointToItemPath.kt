package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

abstract class CxxRemoveWaypointToItemPath(): IMessage {

    var index: Int = 0

    constructor(index: Int): this() {
        this.index = index
    }


    override fun fromBytes(buf: ByteBuf) {
        index = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(index)
    }

    abstract class Handler<T: CxxRemoveWaypointToItemPath, UpdateResponse: IMessage>: MBMessageHandler<T, IMessage?> {
        abstract val item: ItemPath
        abstract fun getStack(message: T, ctx: MessageContext): ItemStack?
        abstract fun createResponse(message: T, ctx: MessageContext, waypointList: NBTTagList): UpdateResponse?
        override val receiverSide = Side.SERVER

        override fun onMessage(message: T, ctx: MessageContext): IMessage? {
            val stack = getStack(message, ctx) ?: return null
            val data = item.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            if(message.index < data.tagCount())
                data.removeTag(message.index)
            val answer = createResponse(message, ctx, data)
            MoarBoats.network.sendToAll(answer)
            return null
        }

    }
}