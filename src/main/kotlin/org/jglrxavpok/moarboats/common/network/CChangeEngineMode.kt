package org.jglrxavpok.moarboats.common.network

import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.BaseEngineModule

class CChangeEngineMode(): MoarBoatsPacket {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    private var locked: Boolean = false

    constructor(boatID: Int, moduleLocation: ResourceLocation, locked: Boolean): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
        this.locked = locked
    }

    object Handler: MBMessageHandler<CChangeEngineMode, MoarBoatsPacket?> {
        override val packetClass = CChangeEngineMode::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CChangeEngineMode, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val level = player.level
            val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            module as BaseEngineModule
            module.setStationary(boat, message.locked)
            return null
        }
    }

}