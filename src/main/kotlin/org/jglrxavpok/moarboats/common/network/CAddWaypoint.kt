package org.jglrxavpok.moarboats.common.network

import net.minecraft.util.math.BlockPos
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CAddWaypoint(): MoarBoatsPacket {

    var x: Int = 0
    var z: Int = 0
    var boatID: Int = 0
    @MoarBoatsPacket.Nullable
    var boost: Double? = null

    constructor(blockPos: BlockPos, boatID: Int, boost: Double?): this() {
        x = blockPos.x
        z = blockPos.z
        this.boatID = boatID
        this.boost = boost
    }

    object Handler: MBMessageHandler<CAddWaypoint, MoarBoatsPacket> {
        override val packetClass = CAddWaypoint::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CAddWaypoint, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val level = player.world
            val boat = level.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            HelmModule.addWaypoint(boat,
                    message.x,
                    message.z,
                    message.boost)
            return null
        }


    }
}