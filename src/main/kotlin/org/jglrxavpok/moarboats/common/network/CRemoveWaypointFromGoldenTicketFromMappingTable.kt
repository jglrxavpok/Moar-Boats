package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.ListNBT
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class CRemoveWaypointFromGoldenTicketFromMappingTable: CxxRemoveWaypointToItemPath {

    constructor()

    var tileEntityX: Int = 0
    var tileEntityY: Int = 0
    var tileEntityZ: Int = 0

    constructor(index: Int, mappingTable: TileEntityMappingTable): super(index) {
        this.tileEntityX = mappingTable.blockPos.x
        this.tileEntityY = mappingTable.blockPos.y
        this.tileEntityZ = mappingTable.blockPos.z
    }

    object Handler: CxxRemoveWaypointToItemPath.Handler<CRemoveWaypointFromGoldenTicketFromMappingTable, SSetGoldenItinerary>() {
        override val item = ItemGoldenTicket
        override val packetClass = CRemoveWaypointFromGoldenTicketFromMappingTable::class.java

        override fun getStack(message: CRemoveWaypointFromGoldenTicketFromMappingTable, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val pos = BlockPos.Mutable(tileEntityX, tileEntityY, tileEntityZ)
                val te = ctx.sender!!.level.getBlockEntity(pos)
                val stack = when(te) {
                    is TileEntityMappingTable -> {
                        te.inventory.getItem(0)
                    }
                    else -> {
                        MoarBoats.logger.error("Invalid tile entity when trying to remove waypoint at $pos")
                        null
                    }
                }
                return stack
            }
        }

        override fun createResponse(message: CRemoveWaypointFromGoldenTicketFromMappingTable, ctx: NetworkEvent.Context, waypointList: ListNBT): SSetGoldenItinerary? {
            val stack = getStack(message, ctx) ?: return null
            val data = ItemGoldenTicket.getData(stack)
            return SSetGoldenItinerary(data)
        }

    }
}