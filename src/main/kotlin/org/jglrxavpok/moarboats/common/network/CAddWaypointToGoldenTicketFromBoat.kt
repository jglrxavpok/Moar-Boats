package org.jglrxavpok.moarboats.common.network

import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraft.core.BlockPos
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CAddWaypointToGoldenTicketFromBoat: CxxAddWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(pos: BlockPos, boost: Double?, insertionIndex: Int?, boatID: Int): super(pos, boost, insertionIndex) {
        this.boatID = boatID
    }

    object Handler: CxxAddWaypointToItemPath.Handler<CAddWaypointToGoldenTicketFromBoat, SSetGoldenItinerary>() {
        override val item = MBItems.ItemGoldenTicket.get()
        override val packetClass = CAddWaypointToGoldenTicketFromBoat::class.java

        override fun getStack(message: CAddWaypointToGoldenTicketFromBoat, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val player = ctx.sender!!
                val level = player.level
                val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getItem(0)
            }
        }

        override fun createResponse(message: CAddWaypointToGoldenTicketFromBoat, ctx: NetworkEvent.Context, waypointList: ListTag): SSetGoldenItinerary? {
            val stack = getStack(message, ctx) ?: return null
            val data = ItemGoldenTicket.getData(stack)
            return SSetGoldenItinerary(data, false)
        }

    }
}