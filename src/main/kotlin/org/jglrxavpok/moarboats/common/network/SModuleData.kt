package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class SModuleData(): ServerMoarBoatsPacket {

    var data = CompoundTag()

    var boatID: Int = 0

    constructor(boatID: Int, data: CompoundTag): this() {
        this.boatID = boatID
        this.data = data
    }

    object Handler: MBMessageHandler<SModuleData, MoarBoatsPacket> {
        override val packetClass = SModuleData::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SModuleData, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val level = Minecraft.getInstance().level
            val boat = level!!.getEntity(message.boatID) as? ModularBoatEntity ?: return null
            boat.moduleData = message.data
            return null
        }
    }
}