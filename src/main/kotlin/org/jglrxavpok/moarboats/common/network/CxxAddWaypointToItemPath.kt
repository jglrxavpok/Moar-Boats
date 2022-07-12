package org.jglrxavpok.moarboats.common.network

import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraft.core.BlockPos
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

abstract class CxxAddWaypointToItemPath(): MoarBoatsPacket {

    var x: Int = 0
    var z: Int = 0

    @MoarBoatsPacket.Nullable
    var boost: Double? = null
    @MoarBoatsPacket.Nullable
    var insertionIndex: Int? = null

    constructor(pos: BlockPos, boost: Double?, insertionIndex: Int?): this() {
        x = pos.x
        z = pos.z
        this.boost = boost
        this.insertionIndex = insertionIndex
    }


    abstract class Handler<T: CxxAddWaypointToItemPath, UpdateResponse: MoarBoatsPacket>: MBMessageHandler<T, MoarBoatsPacket?> {
        abstract val item: ItemPath
        abstract fun getStack(message: T, ctx: NetworkEvent.Context): ItemStack?
        abstract fun createResponse(message: T, ctx: NetworkEvent.Context, waypointList: ListTag): UpdateResponse?
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: T, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val stack = getStack(message, ctx) ?: return null
            val data = item.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            HelmModule.addWaypointToList(data,
                    message.x,
                    message.z,
                    message.boost,
                    message.insertionIndex)
            val answer = createResponse(message, ctx, data)
            MoarBoats.network.send(PacketDistributor.ALL.noArg(), answer)
            return null
        }

    }
}