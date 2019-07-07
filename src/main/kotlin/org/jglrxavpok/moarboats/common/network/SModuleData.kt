package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class SModuleData(): MoarBoatsPacket {

    var data = NBTTagCompound()

    var boatID: Int = 0

    constructor(boatID: Int, data: NBTTagCompound): this() {
        this.boatID = boatID
        this.data = data
    }

    object Handler: MBMessageHandler<SModuleData, MoarBoatsPacket> {
        override val packetClass = SModuleData::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SModuleData, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val world = Minecraft.getInstance().world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            boat.moduleData = message.data
            return null
        }
    }
}