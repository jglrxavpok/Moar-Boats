package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CAddWaypointToGoldenTicketFromBoat: CxxAddWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(pos: BlockPos, boost: Double?, insertionIndex: Int?, boatID: Int): super(pos, boost, insertionIndex) {
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

    object Handler: CxxAddWaypointToItemPath.Handler<CAddWaypointToGoldenTicketFromBoat, SSetGoldenItinerary>() {
        override val item = ItemGoldenTicket
        override val packetClass = CAddWaypointToGoldenTicketFromBoat::class

        override fun getStack(message: CAddWaypointToGoldenTicketFromBoat, ctx: MessageContext): ItemStack? {
            with(message) {
                val player = ctx.serverHandler.player
                val world = player.world
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getStackInSlot(0)
            }
        }

        override fun createResponse(message: CAddWaypointToGoldenTicketFromBoat, ctx: MessageContext, waypointList: NBTTagList): SSetGoldenItinerary? {
            val stack = getStack(message, ctx) ?: return null
            val data = ItemGoldenTicket.getData(stack)
            return SSetGoldenItinerary(data)
        }

    }
}