package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
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
        this.tileEntityX = mappingTable.pos.x
        this.tileEntityY = mappingTable.pos.y
        this.tileEntityZ = mappingTable.pos.z
    }

    object Handler: CxxRemoveWaypointToItemPath.Handler<CRemoveWaypointFromGoldenTicketFromMappingTable, SSetGoldenItinerary>() {
        override val item = ItemGoldenTicket
        override val packetClass = CRemoveWaypointFromGoldenTicketFromMappingTable::class.java

        override fun getStack(message: CRemoveWaypointFromGoldenTicketFromMappingTable, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val pos = BlockPos.PooledMutableBlockPos.retain(tileEntityX, tileEntityY, tileEntityZ)
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

        override fun createResponse(message: CRemoveWaypointFromGoldenTicketFromMappingTable, ctx: NetworkEvent.Context, waypointList: NBTTagList): SSetGoldenItinerary? {
            val stack = getStack(message, ctx) ?: return null
            val data = ItemGoldenTicket.getData(stack)
            return SSetGoldenItinerary(data)
        }

    }
}