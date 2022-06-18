package org.jglrxavpok.moarboats.common.network

import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.PacketDistributor
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
        abstract fun createResponse(message: T, ctx: NetworkEvent.Context, waypointList: ListTag): UpdateResponse?
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: T, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val stack = getStack(message, ctx) ?: return null
            val data = item.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            if(message.index < data.size)
                data.removeAt(message.index)
            val answer = createResponse(message, ctx, data)
            MoarBoats.network.send(PacketDistributor.ALL.noArg(), answer)
            return null
        }

    }
}