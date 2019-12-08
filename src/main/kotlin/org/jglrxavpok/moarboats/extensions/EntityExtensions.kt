package org.jglrxavpok.moarboats.extensions

import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.EntityDataManager
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import java.lang.reflect.Field
import java.lang.reflect.Method

private lateinit var getEntry: Method
private lateinit var dirtyField: Field

fun <T> EntityDataManager.setDirty(key: DataParameter<T>) {
    if(! ::getEntry.isInitialized) {
        getEntry = ObfuscationReflectionHelper.findMethod(EntityDataManager::class.java, "func_187219_c", DataParameter::class.java)
        dirtyField = ObfuscationReflectionHelper.findField(EntityDataManager::class.java, "field_187237_f")
    }
    (getEntry(this, key) as EntityDataManager.DataEntry<T>).isDirty = true
    dirtyField[this] = true
}