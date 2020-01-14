package org.jglrxavpok.moarboats.common.network

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

class CShowBoatMenu: MoarBoatsPacket {

    object Handler: MBMessageHandler<CShowBoatMenu, MoarBoatsPacket?> {
        override val packetClass = CShowBoatMenu::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CShowBoatMenu, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            if(player.ridingEntity is BasicBoatEntity) {
                val boat = player.ridingEntity as BasicBoatEntity
                boat.openGuiIfPossible(player)
            }
            return null
        }

    }
}