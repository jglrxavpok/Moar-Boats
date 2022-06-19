package org.jglrxavpok.moarboats

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.DimensionDataStorage

interface Proxy {

    fun get(dimensionType: ResourceKey<Level>): DimensionDataStorage

}