package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.client.gui.GuiFluid
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class S19UpdateFluidGui(): IMessage {

    var fluidAmount = 0
    var fluidName = ""

    constructor(fluidName: String, fluidAmount: Int): this() {
        this.fluidName = fluidName
        this.fluidAmount = fluidAmount
    }

    override fun fromBytes(buf: ByteBuf) {
        fluidName = ByteBufUtils.readUTF8String(buf)
        fluidAmount = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(fluidAmount)
        ByteBufUtils.writeUTF8String(buf, fluidName)
    }

    object Handler: IMessageHandler<S19UpdateFluidGui, IMessage> {
        override fun onMessage(message: S19UpdateFluidGui, ctx: MessageContext): IMessage? {
            val screen = Minecraft.getMinecraft().currentScreen
            if(screen is GuiFluid) {
                screen.updateFluid(message.fluidName, message.fluidAmount)
            }
            return null
        }
    }
}