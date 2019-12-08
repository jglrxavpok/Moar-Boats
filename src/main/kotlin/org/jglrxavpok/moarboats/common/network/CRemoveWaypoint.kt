package org.jglrxavpok.moarboats.common.network

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CRemoveWaypoint(): MoarBoatsPacket {

    var waypointIndex: Int = 0
    var boatID: Int = 0

    constructor(waypointIndex: Int, boatID: Int): this() {
        this.boatID = boatID
        this.waypointIndex = waypointIndex
    }

    object Handler: MBMessageHandler<CRemoveWaypoint, MoarBoatsPacket> {
        override val packetClass = CRemoveWaypoint::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CRemoveWaypoint, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val level = player.world
            val boat = level.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            HelmModule.removeWaypoint(boat, message.waypointIndex)
            return null
        }
    }
}