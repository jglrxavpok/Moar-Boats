package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable
import kotlin.reflect.KClass

class SConfirmWaypointSwap: IMessage {
    override fun fromBytes(buf: ByteBuf?) { }

    override fun toBytes(buf: ByteBuf?) { }

    object Handler: MBMessageHandler<SConfirmWaypointSwap, IMessage?> {
        override val packetClass = SConfirmWaypointSwap::class
        override val receiverSide = Side.CLIENT

        override fun onMessage(message: SConfirmWaypointSwap, ctx: MessageContext): IMessage? {
            if(Minecraft.getMinecraft().currentScreen is GuiMappingTable) {
                val mappingTable = Minecraft.getMinecraft().currentScreen as GuiMappingTable
                mappingTable.confirmSwap()
            }
            return null
        }

    }
}