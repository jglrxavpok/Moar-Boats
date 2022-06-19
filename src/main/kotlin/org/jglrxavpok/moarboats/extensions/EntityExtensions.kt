package org.jglrxavpok.moarboats.extensions

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData

fun <T> SynchedEntityData.setDirty(key: EntityDataAccessor<T>) {
    getItem(key).isDirty = true
    this.isDirty = true
}