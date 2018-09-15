package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.init.Items
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenItinerary
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.modules.DispenserModule
import org.jglrxavpok.moarboats.common.modules.DispensingModule
import org.jglrxavpok.moarboats.common.modules.HelmModule

class C20SaveItineraryToMap(): IMessage {

    private var boatID = 0
    private var moduleID = ResourceLocation(MoarBoats.ModID, "none")

    constructor(boatID: Int, moduleID: ResourceLocation): this() {
        this.boatID = boatID
        this.moduleID = moduleID
    }

    override fun fromBytes(buf: ByteBuf) {
        boatID = buf.readInt()
        moduleID = ResourceLocation(ByteBufUtils.readUTF8String(buf))
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(boatID)
        ByteBufUtils.writeUTF8String(buf, moduleID.toString())
    }

    object Handler: IMessageHandler<C20SaveItineraryToMap, IMessage?> {
        override fun onMessage(message: C20SaveItineraryToMap, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleID
            val module = BoatModuleRegistry[moduleLocation].module
            module as HelmModule
            val list = module.waypointsProperty[boat].copy()
            val inv = boat.getInventory(module)
            if(inv.getStackInSlot(0).item == Items.FILLED_MAP) {
                inv.setInventorySlotContents(0, ItemMapWithPath.createStack(list))
            }
            return null
        }
    }
}