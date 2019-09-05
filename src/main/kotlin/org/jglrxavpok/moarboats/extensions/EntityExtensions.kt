package org.jglrxavpok.moarboats.extensions

import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.EntityDataManager

fun <T> EntityDataManager.setDirty(key: DataParameter<T>) {
    this.getItem(key).isDirty = true
    this.isDirty = true
}