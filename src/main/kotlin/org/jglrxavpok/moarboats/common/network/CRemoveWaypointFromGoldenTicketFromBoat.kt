package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CRemoveWaypointFromGoldenTicketFromBoat: CxxRemoveWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(index: Int, boatID: Int): super(index) {
        this.boatID = boatID
    }

    override fun fromBytes(buf: ByteBuf) {
        super.fromBytes(buf)
        boatID = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        super.toBytes(buf)
        buf.writeInt(boatID)
    }

    object Handler: CxxRemoveWaypointToItemPath.Handler<CRemoveWaypointFromGoldenTicketFromBoat, SSetGoldenItinerary>() {
        override val item = ItemGoldenTicket
        override val packetClass = CRemoveWaypointFromGoldenTicketFromBoat::class

        override fun getStack(message: CRemoveWaypointFromGoldenTicketFromBoat, ctx: MessageContext): ItemStack? {
            with(message) {
                val player = ctx.serverHandler.player
                val world = player.world
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getStackInSlot(0)
            }
        }

        override fun createResponse(message: CRemoveWaypointFromGoldenTicketFromBoat, ctx: MessageContext, waypointList: NBTTagList): SSetGoldenItinerary? {
            val stack = getStack(message, ctx) ?: return null
            val data = ItemGoldenTicket.getData(stack)
            return SSetGoldenItinerary(data)
        }

    }
}