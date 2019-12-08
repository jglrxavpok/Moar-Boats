package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.MapData
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class SMapAnswer(): MoarBoatsPacket {

    var mapName = ""
    var mapData = CompoundNBT()

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    constructor(name: String, boatID: Int, moduleLocation: ResourceLocation): this() {
        this.mapName = name
        this.boatID = boatID
        this.moduleLocation = moduleLocation
    }

    object Handler: MBMessageHandler<SMapAnswer, MoarBoatsPacket> {
        override val packetClass = SMapAnswer::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SMapAnswer, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val mapID = message.mapName
            val data = MapData(mapID)
            data.read(message.mapData)
            val level = Minecraft.getInstance().world
            val boat = level.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            module as HelmModule
            module.receiveMapData(boat, data)
            //Minecraft.getMinecraft().level.setData(mapID, data)
            return null
        }
    }
}