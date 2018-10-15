package org.jglrxavpok.moarboats.integration.journeymap

import net.minecraft.client.Minecraft
import net.minecraft.util.text.TextComponentBase
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.integration.*
import java.io.File

@MoarBoatsIntegration("journeymap")
class JourneyMapPlugin(): MoarBoatsPlugin, IWaypointProvider {

    private val waypointList = mutableListOf<WaypointInfo>()
    override val name = TextComponentTranslation("waypoint_provider.journeymap")

    override fun preInit() {
        WaypointProviders += this
    }

    override fun getList(): List<WaypointInfo> {
        return waypointList
    }

    override fun updateList() {
        waypointList.clear()
        val sessionType = if(Minecraft.getMinecraft().isIntegratedServerRunning) "sp" else "mp"
        var worldName = (Minecraft.getMinecraft().integratedServer?.folderName ?: Minecraft.getMinecraft().world.minecraftServer?.worldName ?: "null")
        val addTilde = worldName.endsWith("-")
        while(worldName.endsWith("-")) {
            worldName = worldName.removeSuffix("-")
        }
        if(addTilde)
            worldName += "~"
        val folder = File("journeymap/data/$sessionType/$worldName/waypoints/")
        println(">> Reading waypoints from ${folder.absolutePath} - ${folder.exists()}")
        folder.listFiles()?.forEach {
            println(">>> $it")
        }
    }
}