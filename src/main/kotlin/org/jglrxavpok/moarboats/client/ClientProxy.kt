package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.DimensionDataStorage
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.ModList
import org.jglrxavpok.moarboats.Proxy
import org.jglrxavpok.moarboats.integrations.littlelogistics.LittleLogisticsEvents
import org.jglrxavpok.moarboats.integrations.littlelogistics.client.LittleLogisticsClientEvents
import org.lwjgl.glfw.GLFW
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

class ClientProxy: Proxy {

    init {
        MOD_CONTEXT.getKEventBus().addListener(ClientEvents::doClientStuff)

        if(ModList.get().isLoaded("littlelogistics")) {
            MinecraftForge.EVENT_BUS.register(LittleLogisticsClientEvents::class.java)
        }
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