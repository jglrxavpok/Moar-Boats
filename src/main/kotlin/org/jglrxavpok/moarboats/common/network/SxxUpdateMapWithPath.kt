package org.jglrxavpok.moarboats.common.network

import net.minecraft.nbt.ListTag
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent

abstract class SxxUpdateMapWithPath: MoarBoatsPacket {

    constructor()

    lateinit var list: ListTag

    constructor(waypointList: ListTag) {
        this.list = waypointList
    }

    abstract class Handler<T: SxxUpdateMapWithPath>: MBMessageHandler<T, MoarBoatsPacket?> {
        abstract fun updatePath(message: T, ctx: NetworkEvent.Context, list: ListTag)
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: T, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val list = message.list
            updatePath(message, ctx, list)
            return null
        }
    }


}