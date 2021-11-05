package org.jglrxavpok.moarboats.common.data

import net.minecraft.nbt.CompoundNBT
import net.minecraft.world.storage.WorldSavedData

class MapImageStripe(val stripeID: String): WorldSavedData(stripeID) {

    var index: Int = 0
    var textureStripe: IntArray = intArrayOf()

    constructor(id: String, index: Int, textureStripe: IntArray): this(id) {
        this.index = index
        this.textureStripe = textureStripe
    }

    override fun save(compound: CompoundNBT): CompoundNBT {
        compound.putInt("index", index)
        compound.putIntArray("stripe", textureStripe)
        return compound
    }

    override fun load(nbt: CompoundNBT) {
        index = nbt.getInt("index")
        textureStripe = nbt.getIntArray("stripe")
    }
}