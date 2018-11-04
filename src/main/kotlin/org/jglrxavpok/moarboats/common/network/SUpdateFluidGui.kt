package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.client.gui.GuiFluid

class SUpdateFluidGui(): IMessage {

    var fluidAmount = 0
    var fluidCapacity = 0
    var fluidName = ""

    constructor(fluidName: String, fluidAmount: Int, fluidCapacity: Int): this() {
        this.fluidName = fluidName
        this.fluidAmount = fluidAmount
        this.fluidCapacity = fluidCapacity
    }

    override fun fromBytes(buf: ByteBuf) {
        fluidAmount = buf.readInt()
        fluidCapacity = buf.readInt()
        fluidName = ByteBufUtils.readUTF8String(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(fluidAmount)
        buf.writeInt(fluidCapacity)
        ByteBufUtils.writeUTF8String(buf, fluidName)
    }

    object Handler: MBMessageHandler<SUpdateFluidGui, IMessage> {
        override val packetClass = SUpdateFluidGui::class
        override val receiverSide = Side.CLIENT

        override fun onMessage(message: SUpdateFluidGui, ctx: MessageContext): IMessage? {
            val screen = Minecraft.getMinecraft().currentScreen
            if(screen is GuiFluid) {
                screen.updateFluid(message.fluidName, message.fluidAmount, message.fluidCapacity)
            }
            return null
        }
    }
}