package org.jglrxavpok.moarboats.common.network

import net.minecraft.nbt.ListNBT
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent

abstract class SxxUpdateMapWithPath: MoarBoatsPacket {

    constructor()

    lateinit var list: ListNBT

    constructor(waypointList: ListNBT) {
        this.list = waypointList
    }

    abstract class Handler<T: SxxUpdateMapWithPath>: MBMessageHandler<T, MoarBoatsPacket?> {
        abstract fun updatePath(message: T, ctx: NetworkEvent.Context, list: ListNBT)
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: T, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val list = message.list
            updatePath(message, ctx, list)
            return null
        }
    }


}