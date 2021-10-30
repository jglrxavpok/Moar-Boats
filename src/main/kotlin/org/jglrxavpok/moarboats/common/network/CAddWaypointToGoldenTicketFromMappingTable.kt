package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.ListNBT
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import java.util.*

class CAddWaypointToGoldenTicketFromMappingTable: CxxAddWaypointToItemPath {

    constructor()

    var tileEntityX: Int = 0
    var tileEntityY: Int = 0
    var tileEntityZ: Int = 0

    constructor(pos: BlockPos, boost: Double?, insertionIndex: Int?, mappingTable: TileEntityMappingTable): super(pos, boost, insertionIndex) {
        this.tileEntityX = mappingTable.blockPos.x
        this.tileEntityY = mappingTable.blockPos.y
        this.tileEntityZ = mappingTable.blockPos.z
    }

    object Handler: CxxAddWaypointToItemPath.Handler<CAddWaypointToGoldenTicketFromMappingTable, SSetGoldenItinerary>() {
        override val item = ItemGoldenTicket
        override val packetClass = CAddWaypointToGoldenTicketFromMappingTable::class.java

        override fun getStack(message: CAddWaypointToGoldenTicketFromMappingTable, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val pos = BlockPos.Mutable(tileEntityX, tileEntityY, tileEntityZ)
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
                if(stack != null) {
                    if(ItemGoldenTicket.isEmpty(stack)) {
                        ItemGoldenTicket.initStack(stack, UUID.randomUUID())
                    }
                }
                return stack
            }
        }

        override fun createResponse(message: CAddWaypointToGoldenTicketFromMappingTable, ctx: NetworkEvent.Context, waypointList: ListNBT): SSetGoldenItinerary? {
            val stack = getStack(message, ctx) ?: return null
            val data = ItemGoldenTicket.getData(stack)
            return SSetGoldenItinerary(data)
        }

    }
}