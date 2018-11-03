package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.DispensingModule

class CChangeDispenserFacing(): IMessage {

    private var boatID = 0
    private var moduleID = ResourceLocation(MoarBoats.ModID, "none")
    private var facing = EnumFacing.SOUTH

    constructor(boatID: Int, moduleID: ResourceLocation, facing: EnumFacing): this() {
        this.boatID = boatID
        this.moduleID = moduleID
        this.facing = facing
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        moduleID = ResourceLocation(ByteBufUtils.readUTF8String(buf))
        facing = EnumFacing.values()[buf.readInt()]
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        ByteBufUtils.writeUTF8String(buf, moduleID.toString())
        buf.writeInt(facing.ordinal)
    }

    object Handler: MBMessageHandler<CChangeDispenserFacing, IMessage?> {
        override val packetClass = CChangeDispenserFacing::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CChangeDispenserFacing, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleID
            val module = BoatModuleRegistry[moduleLocation].module
            module as DispensingModule
            module.facingProperty[boat] = message.facing
            return null
        }
    }
}