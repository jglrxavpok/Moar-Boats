package org.jglrxavpok.moarboats.common.network

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class CModifyWaypoint(): MoarBoatsPacket {

    private lateinit var waypointData: CompoundTag
    private var index = 0
    private var teX = 0
    private var teY = 0
    private var teZ = 0

    constructor(te: TileEntityMappingTable, index: Int, waypointData: CompoundTag): this() {
        this.teX = te.blockPos.x
        this.teY = te.blockPos.y
        this.teZ = te.blockPos.z
        this.index = index
        this.waypointData = waypointData
    }

    object Handler: MBMessageHandler<CModifyWaypoint, MoarBoatsPacket?> {
        override val packetClass = CModifyWaypoint::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CModifyWaypoint, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            with(message) {
                val player = ctx.sender!!
                val level = player.level
                val pos = BlockPos.MutableBlockPos(teX, teY, teZ)
                val te = level.getBlockEntity(pos)
                when(te) {
                    is TileEntityMappingTable -> {
                        val stack = te.inventory.getItem(0)
                        val item = stack.item as ItemPath
                        item.getWaypointData(stack, MoarBoats.getLocalMapStorage()).set(index, waypointData)
                    }
                    else -> null
                }
            }
            return null
        }

    }
}