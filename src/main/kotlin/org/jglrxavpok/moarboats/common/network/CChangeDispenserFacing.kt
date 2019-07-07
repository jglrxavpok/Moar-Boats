package org.jglrxavpok.moarboats.common.network

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.DispensingModule

class CChangeDispenserFacing(): MoarBoatsPacket {

    private var boatID = 0
    private var moduleID = ResourceLocation(MoarBoats.ModID, "none")
    private var facing = EnumFacing.SOUTH

    constructor(boatID: Int, moduleID: ResourceLocation, facing: EnumFacing): this() {
        this.boatID = boatID
        this.moduleID = moduleID
        this.facing = facing
    }

    object Handler: MBMessageHandler<CChangeDispenserFacing, MoarBoatsPacket?> {
        override val packetClass = CChangeDispenserFacing::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CChangeDispenserFacing, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val world = player.world
            val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleID
            val module = BoatModuleRegistry[moduleLocation].module
            module as DispensingModule
            module.facingProperty[boat] = message.facing
            return null
        }
    }
}