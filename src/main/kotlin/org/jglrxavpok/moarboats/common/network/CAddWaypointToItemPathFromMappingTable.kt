package org.jglrxavpok.moarboats.common.network

import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraft.core.BlockPos
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class CAddWaypointToItemPathFromMappingTable: CxxAddWaypointToItemPath {

    constructor()

    var tileEntityX: Int = 0
    var tileEntityY: Int = 0
    var tileEntityZ: Int = 0

    constructor(pos: BlockPos, boost: Double?, insertionIndex: Int?, mappingTable: TileEntityMappingTable): super(pos, boost, insertionIndex) {
        this.tileEntityX = mappingTable.blockPos.x
        this.tileEntityY = mappingTable.blockPos.y
        this.tileEntityZ = mappingTable.blockPos.z
        this.insertionIndex = insertionIndex
    }

    object Handler: CxxAddWaypointToItemPath.Handler<CAddWaypointToItemPathFromMappingTable, SUpdateMapWithPathInMappingTable>() {
        override val item = MapItemWithPath
        override val packetClass = CAddWaypointToItemPathFromMappingTable::class.java

        override fun getStack(message: CAddWaypointToItemPathFromMappingTable, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val pos = BlockPos.MutableBlockPos(tileEntityX, tileEntityY, tileEntityZ)
                val te = ctx.sender!!.level.getBlockEntity(pos)
                val stack = when(te) {
                    is TileEntityMappingTable -> {
                        te.inventory.getItem(0)
                    }
                    else -> {
                        MoarBoats.logger.error("Invalid tile entity when trying to add waypoint at $pos")
                        null
                    }
                }
                return stack
            }
        }

        override fun createResponse(message: CAddWaypointToItemPathFromMappingTable, ctx: NetworkEvent.Context, waypointList: ListTag): SUpdateMapWithPathInMappingTable? {
            return SUpdateMapWithPathInMappingTable(waypointList, message.tileEntityX, message.tileEntityY, message.tileEntityZ)
        }

    }
}