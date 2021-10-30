package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CChangeLoopingStateItemPathBoat: CChangeLoopingStateBase {

    constructor(): super()

    var boatID: Int = -1

    constructor(loopingOptions: LoopingOptions, boatID: Int): super(loopingOptions) {
        this.boatID = boatID
    }

    object Handler: MBMessageHandler<CChangeLoopingStateItemPathBoat, MoarBoatsPacket?> {
        override val packetClass = CChangeLoopingStateItemPathBoat::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CChangeLoopingStateItemPathBoat, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            with(message) {
                val player = ctx.sender!!
                val level = player.level
                val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return null
                val stack = boat.getInventory(HelmModule).getItem(0)
                val item = stack.item
                if(item is ItemPath) {
                    item.setLoopingOptions(stack, message.loopingOption)
                    when(item) {
                        is ItemGoldenTicket -> return SSetGoldenItinerary(item.getData(stack))
                        is MapItemWithPath -> return SUpdateMapWithPathInBoat(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), boatID)
                    }
                }
                return null
            }
        }

    }
}