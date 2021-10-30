package org.jglrxavpok.moarboats.server

import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants

class ForcedChunkList(val list: ListNBT): WorldSavedData("moarboats_forced_chunks") {

    override fun getId(): String {
        return "moarboats_forced_chunks"
    }

    override fun save(compound: CompoundNBT): CompoundNBT {
        compound.put("list", list)
        return compound
    }

    override fun load(nbt: CompoundNBT) {
        list.clear()
        list.addAll(nbt.getList("list", Constants.NBT.TAG_COMPOUND))
    }

    override fun isDirty(): Boolean {
        return true
    }
}