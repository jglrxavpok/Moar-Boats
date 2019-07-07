package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CRemoveWaypointFromMapWithPathFromBoat: CxxRemoveWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(index: Int, boatID: Int): super(index) {
        this.boatID = boatID
    }

    object Handler: CxxRemoveWaypointToItemPath.Handler<CRemoveWaypointFromMapWithPathFromBoat, SUpdateMapWithPathInBoat>() {
        override val item = ItemMapWithPath
        override val packetClass = CRemoveWaypointFromMapWithPathFromBoat::class.java

        override fun getStack(message: CRemoveWaypointFromMapWithPathFromBoat, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val player = ctx.sender!!
                val world = player.world
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getStackInSlot(0)
            }
        }

        override fun createResponse(message: CRemoveWaypointFromMapWithPathFromBoat, ctx: NetworkEvent.Context, waypointList: NBTTagList): SUpdateMapWithPathInBoat? {
            return SUpdateMapWithPathInBoat(waypointList, message.boatID)
        }

    }
}