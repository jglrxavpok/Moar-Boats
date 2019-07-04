package org.jglrxavpok.moarboats.common.data

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.WorldSavedData

class MapImageStripe(val id: String): WorldSavedData(id) {

    var index: Int = 0
    var textureStripe: IntArray = intArrayOf()

    constructor(id: String, index: Int, textureStripe: IntArray): this(id) {
        this.index = index
        this.textureStripe = textureStripe
    }

    override fun write(compound: NBTTagCompound): NBTTagCompound {
        compound.putInt("index", index)
        compound.putIntArray("stripe", textureStripe)
        return compound
    }

    override fun read(nbt: NBTTagCompound) {
        index = nbt.getInt("index")
        textureStripe = nbt.getIntArray("stripe")
    }
}