package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class SModuleLocations(): MoarBoatsPacket {

    var modules = emptyList<ResourceLocation>()

    var boatID: Int = 0

    constructor(boatID: Int, modules: List<ResourceLocation>): this() {
        this.boatID = boatID
        this.modules = modules
    }

    object Handler: MBMessageHandler<SModuleLocations, MoarBoatsPacket> {
        override val packetClass = SModuleLocations::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SModuleLocations, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val level = Minecraft.getInstance().level
            val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return null
            boat.moduleLocations.clear()
            boat.moduleLocations.addAll(message.modules)
            return null
        }
    }
}