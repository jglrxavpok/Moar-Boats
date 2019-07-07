package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CRemoveWaypointFromGoldenTicketFromBoat: CxxRemoveWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(index: Int, boatID: Int): super(index) {
        this.boatID = boatID
    }

    object Handler: CxxRemoveWaypointToItemPath.Handler<CRemoveWaypointFromGoldenTicketFromBoat, SSetGoldenItinerary>() {
        override val item = ItemGoldenTicket
        override val packetClass = CRemoveWaypointFromGoldenTicketFromBoat::class.java

        override fun getStack(message: CRemoveWaypointFromGoldenTicketFromBoat, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val player = ctx.sender!!
                val world = player.world
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getStackInSlot(0)
            }
        }

        override fun createResponse(message: CRemoveWaypointFromGoldenTicketFromBoat, ctx: NetworkEvent.Context, waypointList: NBTTagList): SSetGoldenItinerary? {
            val stack = getStack(message, ctx) ?: return null
            val data = ItemGoldenTicket.getData(stack)
            return SSetGoldenItinerary(data)
        }

    }
}