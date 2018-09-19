package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class S25UpdateMapWithPathInBoat: SxxUpdateMapWithPath {

    var boatID: Int = 0

    constructor()

    constructor(list: NBTTagList, boatID: Int): super(list) {
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

    object Handler: SxxUpdateMapWithPath.Handler<S25UpdateMapWithPathInBoat>() {
        override fun updatePath(message: S25UpdateMapWithPathInBoat, ctx: MessageContext, list: NBTTagList) {
            with(message) {
                val player = Minecraft.getMinecraft().player
                val world = player.world
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return
                val stack = boat.getInventory(HelmModule).getStackInSlot(0)
                if(stack.tagCompound == null) {
                    stack.tagCompound = NBTTagCompound()
                }
                stack.tagCompound!!.setTag("${MoarBoats.ModID}.path", list)
            }
        }

    }
}