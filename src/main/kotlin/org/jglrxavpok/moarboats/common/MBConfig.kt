package org.jglrxavpok.moarboats.common

import net.minecraft.init.Items as MCItems
import net.minecraftforge.common.config.Configuration

object MBConfig {

    internal lateinit var backing: Configuration
    val fishingRemainingUsesBeforeBreak: Int
        get() = backing.getInt("remainingUsesBeforeRemoval", "Fishing", 0, 0, 64,
                "0 means the rods will break and the fishing module won't try to find a replacement in storage (server side only)")
    val fishingSpeedMultiplier: Float
        get() = backing.getFloat("speedMultiplier", "Fishing", 1f, 10e-16f, 100f,
                "The higher, the more frequent the fishing module will fish items (server side only)")

    fun loadAll() {
        backing.load()

        // load them by calling them
        fishingRemainingUsesBeforeBreak
        fishingSpeedMultiplier

        // allows for defaults to be saved on first load
        backing.save()
    }


}