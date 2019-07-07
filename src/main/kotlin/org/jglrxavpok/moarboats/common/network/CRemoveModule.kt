package org.jglrxavpok.moarboats.common.network

import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class CRemoveModule(): MoarBoatsPacket {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    constructor(boatID: Int, moduleLocation: ResourceLocation): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
    }

    object Handler: MBMessageHandler<CRemoveModule, SMapAnswer> {
        override val packetClass = CRemoveModule::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CRemoveModule, ctx: NetworkEvent.Context): SMapAnswer? {
            val player = ctx.sender!!
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            boat.removeModule(moduleLocation)
            player.closeScreen()
            return null
        }
    }
}