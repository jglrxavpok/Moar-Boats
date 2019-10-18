package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.DispensingModule

class CChangeDispenserPeriod(): MoarBoatsPacket {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")
    var period: Double = 0.0

    constructor(boatID: Int, moduleLocation: ResourceLocation, period: Double): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
        this.period = period
    }

    object Handler: MBMessageHandler<CChangeDispenserPeriod, MoarBoatsPacket?> {
        override val packetClass = CChangeDispenserPeriod::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CChangeDispenserPeriod, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val level = player.world
            val boat = level.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            module as DispensingModule
            module.changePeriod(boat, message.period)
            return null
        }
    }

}