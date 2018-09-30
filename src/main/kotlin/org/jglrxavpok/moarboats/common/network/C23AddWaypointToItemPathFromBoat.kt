package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule
import kotlin.reflect.KClass

class C23AddWaypointToItemPathFromBoat: CxxAddWaypointToItemPath {

    var boatID = -1

    constructor()

    constructor(pos: BlockPos, boost: Double?, boatID: Int): super(pos, boost) {
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

    object Handler: CxxAddWaypointToItemPath.Handler<C23AddWaypointToItemPathFromBoat, S25UpdateMapWithPathInBoat>() {
        override val item = ItemMapWithPath
        override val packetClass = C23AddWaypointToItemPathFromBoat::class

        override fun getStack(message: C23AddWaypointToItemPathFromBoat, ctx: MessageContext): ItemStack? {
            with(message) {
                val player = ctx.serverHandler.player
                val world = player.world
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
                return boat.getInventory(HelmModule).getStackInSlot(0)
            }
        }

        override fun createResponse(message: C23AddWaypointToItemPathFromBoat, ctx: MessageContext, waypointList: NBTTagList): S25UpdateMapWithPathInBoat? {
            return S25UpdateMapWithPathInBoat(waypointList, message.boatID)
        }

    }
}