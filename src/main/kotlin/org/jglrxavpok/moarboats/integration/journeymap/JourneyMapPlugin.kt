package org.jglrxavpok.moarboats.integration.journeymap

import org.jglrxavpok.moarboats.integration.MoarBoatsIntegration
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin
import org.jglrxavpok.moarboats.integration.IWaypointProvider
import org.jglrxavpok.moarboats.integration.WaypointProviders

@MoarBoatsIntegration("journeymap")
class JourneyMapPlugin(): MoarBoatsPlugin, IWaypointProvider {
    override fun preInit() {
        WaypointProviders += this
    }
}