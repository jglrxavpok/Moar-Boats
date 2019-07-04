package org.jglrxavpok.moarboats.extensions

import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.EntityDataManager

fun <T> EntityDataManager.setDirty(key: DataParameter<T>) {
    this.getEntry(key).isDirty = true
    this.dirty = true
}