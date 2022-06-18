package org.jglrxavpok.moarboats.server

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.level.saveddata.SavedData

class ForcedChunkList: SavedData {

    companion object {
        fun getId(): String {
            return "moarboats:forced_chunks"
        }
    }

    val list = ListTag()

    constructor() {}

    constructor(list: ListTag) {
        this.list.addAll(list);
    }

    constructor(nbt: CompoundTag) {
        list.clear()
        list.addAll(nbt.getList("list", Tag.TAG_COMPOUND.toInt()))
    }

    override fun save(compound: CompoundTag): CompoundTag {
        compound.put("list", list)
        return compound
    }

    override fun isDirty(): Boolean {
        return true
    }
}