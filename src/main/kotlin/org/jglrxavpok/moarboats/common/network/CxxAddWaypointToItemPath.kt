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

abstract class CxxAddWaypointToItemPath(): IMessage {

    var x: Int = 0
    var z: Int = 0

    var boost: Double? = null

    constructor(pos: BlockPos, boost: Double?): this() {
        x = pos.x
        z = pos.z
        this.boost = boost
    }


    override fun fromBytes(buf: ByteBuf) {
        x = buf.readInt()
        z = buf.readInt()
        if(buf.readBoolean())
            boost = buf.readDouble()
        else
            boost = null
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(z)
        buf.writeBoolean(boost != null)
        if(boost != null)
            buf.writeDouble(boost!!)
    }

    abstract class Handler<T: CxxAddWaypointToItemPath, UpdateResponse: IMessage>: MBMessageHandler<T, IMessage?> {
        abstract val item: ItemPath
        abstract fun getStack(message: T, ctx: MessageContext): ItemStack?
        abstract fun createResponse(message: T, ctx: MessageContext, waypointList: NBTTagList): UpdateResponse?
        override val receiverSide = Side.SERVER

        override fun onMessage(message: T, ctx: MessageContext): IMessage? {
            val stack = getStack(message, ctx) ?: return null
            val data = item.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            HelmModule.addWaypointToList(data,
                    message.x,
                    message.z,
                    message.boost)
            val answer = createResponse(message, ctx, data)
            MoarBoats.network.sendToAll(answer)
            println(">>>")
            return SConfirmWaypointCreation(data)
        }

    }
}