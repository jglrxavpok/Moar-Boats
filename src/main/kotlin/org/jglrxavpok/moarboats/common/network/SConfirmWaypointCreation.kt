package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.ListTag
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable

class SConfirmWaypointCreation(): ServerMoarBoatsPacket {

    private lateinit var data: ListTag

    constructor(data: ListTag): this() {
        this.data = data
    }

    object Handler: MBMessageHandler<SConfirmWaypointCreation, MoarBoatsPacket?> {
        override val packetClass = SConfirmWaypointCreation::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SConfirmWaypointCreation, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            if(Minecraft.getInstance().screen is GuiMappingTable) {
                val mappingTable = Minecraft.getInstance().screen as GuiMappingTable
                mappingTable.confirmWaypointCreation(message.data)
            }
            return null
        }

    }
}