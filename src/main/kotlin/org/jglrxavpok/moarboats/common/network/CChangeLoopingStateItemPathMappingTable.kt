package org.jglrxavpok.moarboats.common.network

import net.minecraft.core.BlockPos
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class CChangeLoopingStateItemPathMappingTable: CChangeLoopingStateBase {

    constructor(): super()

    var teX: Int = -1
    var teY: Int = -1
    var teZ: Int = -1

    constructor(loopingOption: LoopingOptions, mappingTable: TileEntityMappingTable): super(loopingOption) {
        this.teX = mappingTable.blockPos.x
        this.teY = mappingTable.blockPos.y
        this.teZ = mappingTable.blockPos.z
    }

    object Handler: MBMessageHandler<CChangeLoopingStateItemPathMappingTable, MoarBoatsPacket?> {
        override val packetClass = CChangeLoopingStateItemPathMappingTable::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CChangeLoopingStateItemPathMappingTable, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            with(message) {
                val player = ctx.sender!!
                val level = player.level
                val pos = BlockPos.MutableBlockPos(teX, teY, teZ)
                val te = level.getBlockEntity(pos)
                if(te !is TileEntityMappingTable)
                    return null
                val stack = te.inventory.getItem(0)
                val item = stack.item
                if(item is ItemPath) {
                    item.setLoopingOptions(stack, message.loopingOption)
                    when(item) {
                        is ItemGoldenTicket -> return SSetGoldenItinerary(item.getData(stack))
                        is MapItemWithPath -> return SUpdateMapWithPathInMappingTable(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), teX, teY, teZ)
                    }
                }
                return null
            }
        }

    }
}