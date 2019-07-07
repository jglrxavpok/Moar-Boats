package org.jglrxavpok.moarboats.common.network

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CChangeLoopingState(): MoarBoatsPacket {

    var boatID: Int = 0
    var loopingOption: LoopingOptions = LoopingOptions.NoLoop

    constructor(loopingOptions: LoopingOptions, boatID: Int): this() {
        this.boatID = boatID
        this.loopingOption = loopingOptions
    }

    object Handler: MBMessageHandler<CChangeLoopingState, MoarBoatsPacket> {
        override val packetClass = CChangeLoopingState::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CChangeLoopingState, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null

            HelmModule.loopingProperty[boat] = message.loopingOption
            return null
        }
    }
}