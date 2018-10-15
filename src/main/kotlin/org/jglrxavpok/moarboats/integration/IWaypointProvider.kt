package org.jglrxavpok.moarboats.integration

import net.minecraft.util.text.TextComponentBase

val WaypointProviders = mutableListOf<IWaypointProvider>()

interface IWaypointProvider {
    val name: TextComponentBase

    fun getList(): List<WaypointInfo>

    /**
     * Called every second by GuiWaypointEditor in a separate thread. You are free to not do anything there
     */
    fun updateList() {}
}

data class WaypointInfo(val name: String, val x: Int, val z: Int, val boost: Double?)