package org.jglrxavpok.moarboats.extensions

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraftforge.fml.util.ObfuscationReflectionHelper
import java.lang.reflect.Field
import java.lang.reflect.Method

fun <T> SynchedEntityData.setDirty(key: EntityDataAccessor<T>) {
    getItem(key).isDirty = true
    this.isDirty = true
}