package org.jglrxavpok.moarboats.common.network

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.client.ClientEvents
import org.jglrxavpok.moarboats.common.data.MapImageStripe

class SMapImageAnswer(): ServerMoarBoatsPacket {

    var mapName = ""
    var stripeIndex = 0
    var textureStripe = intArrayOf()

    constructor(name: String, stripeIndex: Int, textureStripe: IntArray): this() {
        this.mapName = name
        this.stripeIndex = stripeIndex
        this.textureStripe = textureStripe
    }

    object Handler: MBMessageHandler<SMapImageAnswer, MoarBoatsPacket> {
        override val packetClass = SMapImageAnswer::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SMapImageAnswer, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val mapID = message.mapName
            val id = "moarboats:map_preview/$mapID/${message.stripeIndex}"
            val data = MapImageStripe(id, message.stripeIndex, message.textureStripe)
            ClientEvents.saveMapStripe(data)
            return null
        }
    }
}