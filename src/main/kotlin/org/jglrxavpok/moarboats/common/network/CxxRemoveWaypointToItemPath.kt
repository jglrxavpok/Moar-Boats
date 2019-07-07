package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath

abstract class CxxRemoveWaypointToItemPath(): MoarBoatsPacket {

    var index: Int = 0

    constructor(index: Int): this() {
        this.index = index
    }

    abstract class Handler<T: CxxRemoveWaypointToItemPath, UpdateResponse: MoarBoatsPacket>: MBMessageHandler<T, MoarBoatsPacket?> {
        abstract val item: ItemPath
        abstract fun getStack(message: T, ctx: NetworkEvent.Context): ItemStack?
        abstract fun createResponse(message: T, ctx: NetworkEvent.Context, waypointList: NBTTagList): UpdateResponse?
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: T, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val stack = getStack(message, ctx) ?: return null
            val data = item.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            if(message.index < data.size)
                data.removeTag(message.index)
            val answer = createResponse(message, ctx, data)
            MoarBoats.network.send(PacketDistributor.ALL.noArg(), answer)
            return null
        }

    }
}