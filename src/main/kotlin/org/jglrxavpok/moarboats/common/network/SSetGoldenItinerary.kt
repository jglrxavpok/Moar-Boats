package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.world.level.storage.DimensionDataStorage
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket

class SSetGoldenItinerary(): ServerMoarBoatsPacket {

    lateinit var data: ItemGoldenTicket.WaypointData
    var openEditMenuOfMappingTable: Boolean = false

    constructor(data: ItemGoldenTicket.WaypointData, openEditMenuOfMappingTable: Boolean): this() {
        this.data = data
        this.openEditMenuOfMappingTable = openEditMenuOfMappingTable
    }

    object Handler: MBMessageHandler<SSetGoldenItinerary, MoarBoatsPacket?> {
        override val packetClass = SSetGoldenItinerary::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SSetGoldenItinerary, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val mapStorage: DimensionDataStorage = MoarBoats.getLocalMapStorage()
            mapStorage.set(ItemGoldenTicket.WaypointData.makeKey(message.data), message.data)
            message.data.isDirty = true

            val mc = Minecraft.getInstance()

            if(mc.screen is GuiMappingTable) {
                if(message.openEditMenuOfMappingTable) {
                    val mappingTable = Minecraft.getInstance().screen as GuiMappingTable
                    mappingTable.confirmWaypointCreation(message.data.backingList)
                } else {
                    (mc.screen as GuiMappingTable).reload()
                }
            }
            return null
        }
    }
}