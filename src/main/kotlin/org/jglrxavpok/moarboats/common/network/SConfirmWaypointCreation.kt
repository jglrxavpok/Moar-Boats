package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable

class SConfirmWaypointCreation(): MoarBoatsPacket {

    private lateinit var data: ListNBT

    constructor(data: ListNBT): this() {
        this.data = data
    }

    object Handler: MBMessageHandler<SConfirmWaypointCreation, MoarBoatsPacket?> {
        override val packetClass = SConfirmWaypointCreation::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SConfirmWaypointCreation, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            if(Minecraft.getInstance().currentScreen is GuiMappingTable) {
                val mappingTable = Minecraft.getInstance().currentScreen as GuiMappingTable
                mappingTable.confirmWaypointCreation(message.data)
            }
            return null
        }

    }
}