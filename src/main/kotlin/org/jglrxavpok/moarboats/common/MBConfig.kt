package org.jglrxavpok.moarboats.common

import net.minecraft.init.Items as MCItems
import net.minecraftforge.common.config.Configuration
import org.jglrxavpok.moarboats.extensions.k

object MBConfig {

    internal lateinit var backing: Configuration
    val fishingRemainingUsesBeforeBreak: Int
        get() = backing.getInt("remainingUsesBeforeRemoval", "Fishing", 0, 0, 64,
                "0 means the rods will break and the fishing module won't try to find a replacement in storage (server side only)")
    val fishingSpeedMultiplier: Float
        get() = backing.getFloat("speedMultiplier", "Fishing", 1f, 10e-16f, 100f,
                "The higher, the more frequent the fishing module will fish items (server side only)")
    val energyLoaderMaxEnergy: Int
        get() = backing.getInt("maxEnergy", "Energy Loader", 5.k, 1, Integer.MAX_VALUE,
                "The total amount of energy a single energy boat loader can hold at once")
    val energyLoaderSendAmount: Int
        get() = backing.getInt("sendAmount", "Energy Loader", 200, 1, Integer.MAX_VALUE,
                "The total amount of energy a single energy boat loader can send to entities at once (per tick)")
    val energyLoaderPullAmount: Int
        get() = backing.getInt("pullAmount", "Energy Loader", 200, 1, Integer.MAX_VALUE,
                "The total amount of energy a single energy boat loader can pull from neighboring blocks (cables, ducts, generators, etc.) in total (per tick)")
    val energyUnloaderMaxEnergy: Int
        get() = backing.getInt("maxEnergy", "Energy Unloader", 5.k, 1, Integer.MAX_VALUE,
                "The total amount of energy a single energy boat unloader can hold at once")
    val energyUnloaderPullAmount: Int
        get() = backing.getInt("pullAmount", "Energy Unloader", 200, 1, Integer.MAX_VALUE,
                "The total amount of energy a single energy boat unloader can pull from entities at once (per tick)")
    val energyUnloaderSendAmount: Int
        get() = backing.getInt("sendAmount", "Energy Unloader", 200, 1, Integer.MAX_VALUE,
                "The total amount of energy a single energy boat unloader can send to neighboring blocks (cables, ducts, generators, etc.) in total (per tick)")

    fun loadAll() {
        backing.load()

        // load them by calling them
        fishingRemainingUsesBeforeBreak
        fishingSpeedMultiplier
        energyLoaderMaxEnergy
        energyLoaderPullAmount
        energyLoaderSendAmount
        energyUnloaderMaxEnergy
        energyUnloaderPullAmount
        energyUnloaderSendAmount

        // allows for defaults to be saved on first load
        backing.save()
    }


}