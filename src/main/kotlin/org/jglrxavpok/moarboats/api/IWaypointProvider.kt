package org.jglrxavpok.moarboats.api

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

val WaypointProviders = mutableListOf<IWaypointProvider>()

interface IWaypointProvider {
    val name: Component

    fun getList(): List<WaypointInfo>

    /**
     * Called every second by GuiWaypointEditor in a separate thread. You are free to not do anything there
     */
    fun updateList(player: Player) {}
}

data class WaypointInfo(val origin: String, val name: String, val x: Int, val z: Int, val boost: Double?)