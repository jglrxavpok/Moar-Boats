package org.jglrxavpok.moarboats.extensions

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraftforge.fml.util.ObfuscationReflectionHelper
import java.lang.reflect.Field
import java.lang.reflect.Method

private lateinit var getEntry: Method
private lateinit var dirtyField: Field

fun <T> SynchedEntityData.setDirty(key: EntityDataAccessor<T>) {
    if(! ::getEntry.isInitialized) {
        // TODO: replace with access transformer
        getEntry = ObfuscationReflectionHelper.findMethod(SynchedEntityData::class.java, "func_187219_c", EntityDataAccessor::class.java)
        dirtyField = ObfuscationReflectionHelper.findField(SynchedEntityData::class.java, "field_187237_f")
    }
    (getEntry(this, key) as SynchedEntityData.DataItem<T>).isDirty = true
    dirtyField[this] = true
}