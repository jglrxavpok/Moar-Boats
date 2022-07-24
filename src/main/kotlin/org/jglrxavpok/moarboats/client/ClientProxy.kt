package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.DimensionDataStorage
import org.jglrxavpok.moarboats.Proxy
import org.lwjgl.glfw.GLFW
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

class ClientProxy: Proxy {

    init {
        MOD_CONTEXT.getKEventBus().addListener(ClientEvents::doClientStuff)
    }

    override fun showDetailedTooltip(): Boolean {
        return Screen.hasShiftDown()
    }

    override fun get(dimensionType: ResourceKey<Level>): DimensionDataStorage {
        return when {
            Minecraft.getInstance().singleplayerServer != null /* LAN */ -> Minecraft.getInstance().singleplayerServer!!.getLevel(dimensionType)?.dataStorage
                ?: error("Tried to get save data of an nonexistent dimension type? $dimensionType")
            else -> DummyDimensionSavedDataManager
        }
    }
}