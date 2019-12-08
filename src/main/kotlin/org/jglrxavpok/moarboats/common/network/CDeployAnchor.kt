package org.jglrxavpok.moarboats.common.network

import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.AnchorModule

class CDeployAnchor(): MoarBoatsPacket {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    constructor(boatID: Int, moduleLocation: ResourceLocation): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
    }

    object Handler: MBMessageHandler<CDeployAnchor, MoarBoatsPacket?> {
        override val packetClass = CDeployAnchor::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CDeployAnchor, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val level = player.world
            val boat = level.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            module as AnchorModule
            module.deploy(boat, player)
            return null
        }
    }

}