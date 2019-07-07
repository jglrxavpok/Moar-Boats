package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CAddWaypointToItemPathFromBoat: CxxAddWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(pos: BlockPos, boost: Double?, insertionIndex: Int?, boatID: Int): super(pos, boost, insertionIndex) {
        this.boatID = boatID
    }

    object Handler: CxxAddWaypointToItemPath.Handler<CAddWaypointToItemPathFromBoat, SUpdateMapWithPathInBoat>() {
        override val item = ItemMapWithPath
        override val packetClass = CAddWaypointToItemPathFromBoat::class.java

        override fun getStack(message: CAddWaypointToItemPathFromBoat, ctx: NetworkEvent.Context): ItemStack? {
            with(message) {
                val player = ctx.sender!!
                val world = player.world
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getStackInSlot(0)
            }
        }

        override fun createResponse(message: CAddWaypointToItemPathFromBoat, ctx: NetworkEvent.Context, waypointList: NBTTagList): SUpdateMapWithPathInBoat? {
            return SUpdateMapWithPathInBoat(waypointList, message.boatID)
        }

    }
}