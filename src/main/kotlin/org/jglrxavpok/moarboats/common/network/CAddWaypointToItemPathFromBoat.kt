package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.ListNBT
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CAddWaypointToItemPathFromBoat: CxxAddWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(pos: BlockPos, boost: Double?, insertionIndex: Int?, boatID: Int): super(pos, boost, insertionIndex) {
        this.boatID = boatID
    }

    object Handler: CxxAddWaypointToItemPath.Handler<CAddWaypointToItemPathFromBoat, SUpdateMapWithPathInBoat>() {
        override val item = MapItemWithPath
        override val packetClass = CAddWaypointToItemPathFromBoat::class.java

        override fun getStack(message: CAddWaypointToItemPathFromBoat, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val player = ctx.sender!!
                val level = player.world
                val boat = level.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getStackInSlot(0)
            }
        }

        override fun createResponse(message: CAddWaypointToItemPathFromBoat, ctx: NetworkEvent.Context, waypointList: ListNBT): SUpdateMapWithPathInBoat? {
            return SUpdateMapWithPathInBoat(waypointList, message.boatID)
        }

    }
}