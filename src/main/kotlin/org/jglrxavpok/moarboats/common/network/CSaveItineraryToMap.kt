package org.jglrxavpok.moarboats.common.network

import net.minecraft.world.item.MapItem
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule

class CSaveItineraryToMap(): MoarBoatsPacket {

    private var boatID = 0
    private var moduleID = ResourceLocation(MoarBoats.ModID, "none")

    constructor(boatID: Int, moduleID: ResourceLocation): this() {
        this.boatID = boatID
        this.moduleID = moduleID
    }

    object Handler: MBMessageHandler<CSaveItineraryToMap, MoarBoatsPacket?> {
        override val packetClass = CSaveItineraryToMap::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CSaveItineraryToMap, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            if(player.containerMenu !is ContainerHelmModule) {
                MoarBoats.logger.warn("Player $player tried to save an itinerary to a map while not in a helm container, they might be lagging or cheating")
                return null
            }
            val world = player.level
            val boat = world.getEntity(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleID
            val module = BoatModuleRegistry[moduleLocation].module
            module as HelmModule
            val list = module.waypointsProperty[boat].copy()
            val inv = boat.getInventory(module)
            if(inv.getItem(0).item == Items.FILLED_MAP) {
                val id = MapItem.getMapId(inv.getItem(0))
                inv.setItem(0, MapItemWithPath.createStack(list, "map_$id", module.loopingProperty[boat]))
                player.containerMenu.broadcastChanges()
            }
            return null
        }
    }
}