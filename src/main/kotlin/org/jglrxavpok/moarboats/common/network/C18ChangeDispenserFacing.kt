package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.DispenserModule

class C18ChangeDispenserFacing(): IMessage {

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

    object Handler: IMessageHandler<C18ChangeDispenserFacing, IMessage?> {
        override fun onMessage(message: C18ChangeDispenserFacing, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleID
            val module = BoatModuleRegistry[moduleLocation].module
            module as DispenserModule
            module.facingProperty[boat] = message.facing
            return null
        }
    }
}