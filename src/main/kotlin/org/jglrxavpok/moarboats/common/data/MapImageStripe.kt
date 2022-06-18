package org.jglrxavpok.moarboats.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.saveddata.SavedData

class MapImageStripe {

    val stripeID: String
    var index: Int = 0
    var textureStripe: IntArray = intArrayOf()

    constructor(id: String, index: Int, textureStripe: IntArray) {
        this.stripeID = id
        this.index = index
        this.textureStripe = textureStripe
    }

    fun save(compound: CompoundTag): CompoundTag {
        compound.putInt("index", index)
        compound.putIntArray("stripe", textureStripe)
        return compound
    }
}