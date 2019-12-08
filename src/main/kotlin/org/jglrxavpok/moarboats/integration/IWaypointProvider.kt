package org.jglrxavpok.moarboats.integration

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.TextComponent

val WaypointProviders = mutableListOf<IWaypointProvider>()

interface IWaypointProvider {
    val name: TextComponent

    fun getList(): List<WaypointInfo>

    /**
     * Called every second by GuiWaypointEditor in a separate thread. You are free to not do anything there
     */
    fun updateList(player: PlayerEntity) {}
}

data class WaypointInfo(val name: String, val x: Int, val z: Int, val boost: Double?)