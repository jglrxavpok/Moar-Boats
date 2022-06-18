package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.world.storage.DimensionDataStorage
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket

class SSetGoldenItinerary(): MoarBoatsPacket {

    lateinit var data: ItemGoldenTicket.WaypointData

    constructor(data: ItemGoldenTicket.WaypointData): this() {
        this.data = data
    }

    object Handler: MBMessageHandler<SSetGoldenItinerary, MoarBoatsPacket?> {
        override val packetClass = SSetGoldenItinerary::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SSetGoldenItinerary, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val mapStorage: DimensionDataStorage = MoarBoats.getLocalMapStorage()
            mapStorage.set(message.data)
            message.data.isDirty = true

            val mc = Minecraft.getInstance()

            if(mc.screen is GuiMappingTable) {
                (mc.screen as GuiMappingTable).reload()
            }
            return null
        }
    }
}