package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.client.gui.GuiFluid

class SUpdateFluidGui(): MoarBoatsPacket {

    var fluidAmount = 0
    var fluidCapacity = 0
    var fluidName = ""

    constructor(fluidName: String, fluidAmount: Int, fluidCapacity: Int): this() {
        this.fluidName = fluidName
        this.fluidAmount = fluidAmount
        this.fluidCapacity = fluidCapacity
    }

    object Handler: MBMessageHandler<SUpdateFluidGui, MoarBoatsPacket> {
        override val packetClass = SUpdateFluidGui::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SUpdateFluidGui, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val screen = Minecraft.getInstance().screen
            if(screen is GuiFluid) {
                screen.updateFluid(message.fluidName, message.fluidAmount, message.fluidCapacity)
            }
            return null
        }
    }
}