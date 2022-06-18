package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class SMapAnswer(): MoarBoatsPacket {

    var mapID: Int = -1
    var mapData = CompoundTag()

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    constructor(mapID: Int, boatID: Int, moduleLocation: ResourceLocation): this() {
        this.mapID = mapID
        this.boatID = boatID
        this.moduleLocation = moduleLocation
    }

    object Handler: MBMessageHandler<SMapAnswer, MoarBoatsPacket> {
        override val packetClass = SMapAnswer::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SMapAnswer, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val data = MapItemSavedData.load(message.mapData)
            val level = Minecraft.getInstance().level
            val boat = level!!.getEntity(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            module as HelmModule
            module.receiveMapData(boat, data)
            return null
        }
    }
}