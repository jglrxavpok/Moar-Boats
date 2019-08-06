package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.storage.WorldSavedDataStorage
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
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
            val mapStorage: WorldSavedDataStorage = MoarBoats.getLocalMapStorage()
            mapStorage.set(DimensionType.OVERWORLD, message.data.uuid, message.data)
            message.data.isDirty = true

            val mc = Minecraft.getInstance()

            if(mc.currentScreen is GuiMappingTable) {
                (mc.currentScreen as GuiMappingTable).reload()
            }
            return null
        }
    }
}