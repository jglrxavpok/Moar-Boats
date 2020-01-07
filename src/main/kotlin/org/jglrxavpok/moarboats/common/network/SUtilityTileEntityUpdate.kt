package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity

class SUtilityTileEntityUpdate(): MoarBoatsPacket {

    var data = CompoundNBT()
    var boatID: Int = 0

    constructor(boatID: Int, data: CompoundNBT): this() {
        this.boatID = boatID
        this.data = data
    }

    object Handler: MBMessageHandler<SUtilityTileEntityUpdate, MoarBoatsPacket> {
        override val packetClass = SUtilityTileEntityUpdate::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SUtilityTileEntityUpdate, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val level = Minecraft.getInstance().world
            val boat = level.getEntityByID(message.boatID) as? UtilityBoatEntity<*,*> ?: return null
            boat.updateTileEntity(message.data)
            return null
        }
    }
}