package org.jglrxavpok.moarboats.common.network

import net.minecraft.item.FilledMapItem
import net.minecraft.item.MapItem
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CMapRequest(): MoarBoatsPacket {

    var mapName: String = ""
    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    constructor(name: String, boatID: Int, moduleLocation: ResourceLocation): this() {
        this.mapName = name
        this.boatID = boatID
        this.moduleLocation = moduleLocation
    }

    object Handler: MBMessageHandler<CMapRequest, SMapAnswer> {
        override val packetClass = CMapRequest::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CMapRequest, ctx: NetworkEvent.Context): SMapAnswer? {
            val player = ctx.sender!!
            val level = player.world
            val boat = level.getEntityByID(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            val stack = boat.getInventory(module).getStackInSlot(0)
            val item = stack.item as? FilledMapItem ?: return null // Got request while there was no map!
            val mapName = message.mapName
            val mapdata = FilledMapItem.getData(stack, boat.worldRef)!!
            val packet = SMapAnswer(mapName, message.boatID, message.moduleLocation)
            mapdata.write(packet.mapData)
            module as HelmModule
            module.receiveMapData(boat, mapdata)
            return packet
        }
    }
}