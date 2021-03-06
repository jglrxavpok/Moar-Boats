package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable

class SConfirmWaypointSwap: MoarBoatsPacket {

    object Handler: MBMessageHandler<SConfirmWaypointSwap, MoarBoatsPacket?> {
        override val packetClass = SConfirmWaypointSwap::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SConfirmWaypointSwap, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            if(Minecraft.getInstance().currentScreen is GuiMappingTable) {
                val mappingTable = Minecraft.getInstance().currentScreen as GuiMappingTable
                mappingTable.confirmSwap()
            }
            return null
        }

    }
}