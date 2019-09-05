package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.BaseEngineModule

class CChangeEngineSpeed(): MoarBoatsPacket {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")
    var speed: Float = 0f

    constructor(boatID: Int, moduleLocation: ResourceLocation, speed: Float): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
        this.speed = speed
    }

    object Handler: MBMessageHandler<CChangeEngineSpeed, MoarBoatsPacket?> {
        override val packetClass = CChangeEngineSpeed::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CChangeEngineSpeed, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val level = player.level
            val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            module as BaseEngineModule
            module.changeSpeed(boat, message.speed)
            return null
        }
    }

}