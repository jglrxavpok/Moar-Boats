package org.jglrxavpok.moarboats.common.network

import net.minecraft.core.BlockPos
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import org.jglrxavpok.moarboats.extensions.swap

class CSwapWaypoints(): MoarBoatsPacket {

    private var index1 = -1
    private var index2 = -1
    private var x = -1
    private var y = -1
    private var z = -1

    constructor(index1: Int, index2: Int, pos: BlockPos): this() {
        this.index1 = index1
        this.index2 = index2
        this.x = pos.x
        this.y = pos.y
        this.z = pos.z
    }

    object Handler: MBMessageHandler<CSwapWaypoints, SConfirmWaypointSwap?> {
        override val packetClass = CSwapWaypoints::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CSwapWaypoints, ctx: NetworkEvent.Context): SConfirmWaypointSwap? {
            with(message) {
                val player = ctx.sender!!
                val level = player.level
                val pos = BlockPos.MutableBlockPos(x, y, z)
                val te = level.getBlockEntity(pos)
                return when(te) {
                    is TileEntityMappingTable -> {
                        val stack = te.inventory.getItem(0)
                        val item = stack.item as ItemPath
                        item.getWaypointData(stack, MoarBoats.getLocalMapStorage()).swap(index1, index2)
                        SConfirmWaypointSwap()
                    }
                    else -> null
                }
            }
        }

    }
}