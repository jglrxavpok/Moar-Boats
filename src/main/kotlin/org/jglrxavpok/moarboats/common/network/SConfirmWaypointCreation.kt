package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable

class SConfirmWaypointCreation(): IMessage {

    private lateinit var data: NBTTagList

    constructor(data: NBTTagList): this() {
        this.data = data
    }

    override fun fromBytes(buf: ByteBuf) {
        val nbt = ByteBufUtils.readTag(buf)!!
        data = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND)
    }

    override fun toBytes(buf: ByteBuf) {
        val nbt = NBTTagCompound()
        nbt.setTag("data", data)
        ByteBufUtils.writeTag(buf, nbt)
    }

    object Handler: MBMessageHandler<SConfirmWaypointCreation, IMessage?> {
        override val packetClass = SConfirmWaypointCreation::class
        override val receiverSide = Side.CLIENT

        override fun onMessage(message: SConfirmWaypointCreation, ctx: MessageContext): IMessage? {
            if(Minecraft.getMinecraft().currentScreen is GuiMappingTable) {
                val mappingTable = Minecraft.getMinecraft().currentScreen as GuiMappingTable
                mappingTable.confirmWaypointCreation(message.data)
            }
            return null
        }

    }
}