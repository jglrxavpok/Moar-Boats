package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.ListNBT
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class CRemoveWaypointFromMapWithPathFromMappingTable: CxxRemoveWaypointToItemPath {

    constructor()

    var tileEntityX: Int = 0
    var tileEntityY: Int = 0
    var tileEntityZ: Int = 0

    constructor(index: Int, mappingTable: TileEntityMappingTable): super(index) {
        this.tileEntityX = mappingTable.pos.x
        this.tileEntityY = mappingTable.pos.y
        this.tileEntityZ = mappingTable.pos.z
    }

    object Handler: CxxRemoveWaypointToItemPath.Handler<CRemoveWaypointFromMapWithPathFromMappingTable, SUpdateMapWithPathInMappingTable>() {
        override val item = MapItemWithPath
        override val packetClass = CRemoveWaypointFromMapWithPathFromMappingTable::class.java

        override fun getStack(message: CRemoveWaypointFromMapWithPathFromMappingTable, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val pos = BlockPos.PooledMutable.retain(tileEntityX, tileEntityY, tileEntityZ)
                val te = ctx.sender!!.world.getTileEntity(pos)
                val stack = when(te) {
                    is TileEntityMappingTable -> {
                        te.inventory.getStackInSlot(0)
                    }
                    else -> {
                        MoarBoats.logger.error("Invalid tile entity when trying to add waypoint at $pos")
                        null
                    }
                }
                pos.close()
                return stack
            }
        }

        override fun createResponse(message: CRemoveWaypointFromMapWithPathFromMappingTable, ctx: NetworkEvent.Context, waypointList: ListNBT): SUpdateMapWithPathInMappingTable? {
            return SUpdateMapWithPathInMappingTable(waypointList, message.tileEntityX, message.tileEntityY, message.tileEntityZ)
        }

    }
}