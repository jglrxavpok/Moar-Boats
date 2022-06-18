package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.world.item.RecordItem
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.client.ClientEvents

class SPlayRecordFromBoat(): MoarBoatsPacket {

    var entityID: Int = -1

    @MoarBoatsPacket.Nullable
    var item: RecordItem? = null

    constructor(entityID: Int, item: RecordItem?): this() {
        this.entityID = entityID
        this.item = item
    }

    object Handler: MBMessageHandler<SPlayRecordFromBoat, MoarBoatsPacket> {
        override val packetClass = SPlayRecordFromBoat::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SPlayRecordFromBoat, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = Minecraft.getInstance().player
            ClientEvents.playRecord(player!!.level, message.entityID, message.item)
            return null
        }
    }
}