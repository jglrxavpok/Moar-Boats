package org.jglrxavpok.moarboats.integrations.journeymap

import journeymap.client.api.ClientPlugin
import journeymap.client.api.IClientAPI
import journeymap.client.api.IClientPlugin
import journeymap.client.api.event.ClientEvent
import net.minecraft.network.chat.Component
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IWaypointProvider
import org.jglrxavpok.moarboats.api.WaypointInfo
import org.jglrxavpok.moarboats.api.WaypointProviders

@ClientPlugin
class MoarBoatsPlugin: IClientPlugin, IWaypointProvider {

    private lateinit var apiRef: IClientAPI

    override fun initialize(api: IClientAPI) {
        apiRef = api

        WaypointProviders.add(this)
    }

    override fun getModId(): String {
        return MoarBoats.ModID
    }

    override fun onEvent(event: ClientEvent) {
        // don't care
    }

    // IWaypointProvider
    override val name = Component.literal("JourneyMap")

    override fun getList(): List<WaypointInfo> {
        return apiRef.allWaypoints.map { waypoint ->
            WaypointInfo("JourneyMap", waypoint.name, waypoint.position.x, waypoint.position.z, null)
        }.toList()
    }
}