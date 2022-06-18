package org.jglrxavpok.moarboats.common.network

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.MapItem
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CMapRequest(): MoarBoatsPacket {

    var mapID: Int = -1
    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    constructor(mapID: Int, boatID: Int, moduleLocation: ResourceLocation): this() {
        this.mapID = mapID
        this.boatID = boatID
        this.moduleLocation = moduleLocation
    }

    object Handler: MBMessageHandler<CMapRequest, SMapAnswer> {
        override val packetClass = CMapRequest::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CMapRequest, ctx: NetworkEvent.Context): SMapAnswer? {
            val player = ctx.sender!!
            val level = player.level
            val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            val stack = boat.getInventory(module).getItem(0)
            val item = stack.item as? MapItem ?: return null // Got request while there was no map!
            val mapName = message.mapID
            val mapdata = MapItem.getSavedData(stack, boat.worldRef)!!
            val packet = SMapAnswer(mapName, message.boatID, message.moduleLocation)
            mapdata.save(packet.mapData)
            module as HelmModule
            module.receiveMapData(boat, mapdata)
            return packet
        }
    }
}