package org.jglrxavpok.moarboats.common.network

import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CRemoveWaypointFromMapWithPathFromBoat: CxxRemoveWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(index: Int, boatID: Int): super(index) {
        this.boatID = boatID
    }

    object Handler: CxxRemoveWaypointToItemPath.Handler<CRemoveWaypointFromMapWithPathFromBoat, SUpdateMapWithPathInBoat>() {
        override val item = MBItems.MapItemWithPath.get()
        override val packetClass = CRemoveWaypointFromMapWithPathFromBoat::class.java

        override fun getStack(message: CRemoveWaypointFromMapWithPathFromBoat, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val player = ctx.sender!!
                val level = player.level
                val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getItem(0)
            }
        }

        override fun createResponse(message: CRemoveWaypointFromMapWithPathFromBoat, ctx: NetworkEvent.Context, waypointList: ListTag): SUpdateMapWithPathInBoat? {
            return SUpdateMapWithPathInBoat(waypointList, message.boatID)
        }

    }
}