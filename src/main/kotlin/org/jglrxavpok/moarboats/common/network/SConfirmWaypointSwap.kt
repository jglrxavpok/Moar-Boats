package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable

class SConfirmWaypointSwap: ServerMoarBoatsPacket {

    object Handler: MBMessageHandler<SConfirmWaypointSwap, MoarBoatsPacket?> {
        override val packetClass = SConfirmWaypointSwap::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SConfirmWaypointSwap, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            if(Minecraft.getInstance().screen is GuiMappingTable) {
                val mappingTable = Minecraft.getInstance().screen as GuiMappingTable
                mappingTable.confirmSwap()
            }
            return null
        }

    }
}