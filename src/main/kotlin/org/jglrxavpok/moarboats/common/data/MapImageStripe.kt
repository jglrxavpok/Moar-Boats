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

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("index", index)
        compound.setIntArray("stripe", textureStripe)
        return compound
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        index = nbt.getInteger("index")
        textureStripe = nbt.getIntArray("stripe")
    }
}