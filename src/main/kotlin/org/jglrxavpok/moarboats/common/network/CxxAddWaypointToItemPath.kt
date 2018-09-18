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
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

abstract class CxxAddWaypointToItemPath(): IMessage {

    var x: Int = 0
    var z: Int = 0

    constructor(pos: BlockPos): this() {
        x = pos.x
        z = pos.z
    }


    override fun fromBytes(buf: ByteBuf) {
        x = buf.readInt()
        z = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(z)
    }

    abstract class Handler<T: CxxAddWaypointToItemPath, UpdateResponse: IMessage>: IMessageHandler<T, IMessage?> {
        abstract val item: ItemPath
        abstract fun getStack(message: T, ctx: MessageContext): ItemStack?
        abstract fun createResponse(message: T, waypointList: NBTTagList): UpdateResponse?

        override fun onMessage(message: T, ctx: MessageContext): IMessage? {
            val stack = getStack(message, ctx) ?: return null
            val data = item.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            HelmModule.addWaypointToList(data,
                    message.x,
                    message.z)
            val answer = createResponse(message, data)
            MoarBoats.network.sendToAll(answer)
            return null
        }

    }
}