package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.machine.Arguments
import li.cil.oc.api.machine.Value
import net.minecraft.item.ItemStack

class OCArguments(vararg values: Any?): Arguments {
    val allValues = values.toMutableList()

    override fun isString(p0: Int): Boolean {
        return if(p0 < count()) false else allValues[p0] is String
    }

    override fun checkAny(p0: Int): Any? {
        return allValues[p0]
    }

    override fun checkInteger(p0: Int): Int {
        return allValues[p0] as? Int ?: throw TypeCastException("value is not an integer")
    }

    override fun optBoolean(p0: Int, p1: Boolean): Boolean {
        return if(p0 < count()) checkBoolean(p0) else p1
    }

    override fun count() = allValues.size

    override fun optAny(p0: Int, p1: Any?): Any? {
        return if(p0 < count()) checkAny(p0) else p1
    }

    override fun optItemStack(p0: Int, p1: ItemStack): ItemStack {
        return if(p0 < count()) checkItemStack(p0) else p1
    }

    override fun iterator(): MutableIterator<Any?> {
        return allValues.iterator()
    }

    override fun isTable(p0: Int): Boolean {
        return if(p0 < count()) false else allValues[p0] is MutableMap<*,*>
    }

    override fun optTable(p0: Int, p1: MutableMap<Any?, Any?>): MutableMap<Any?, Any?> {
        return if(p0 < count()) checkTable(p0) else p1
    }

    override fun isByteArray(p0: Int): Boolean {
        return if(p0 < count()) false else allValues[p0] is ByteArray
    }

    override fun optDouble(p0: Int, p1: Double): Double {
        return if(p0 < count()) checkDouble(p0) else p1
    }

    override fun checkString(p0: Int): String {
        return allValues[p0] as? String ?: throw TypeCastException("value is not a string")
    }

    override fun checkItemStack(p0: Int): ItemStack {
        return allValues[p0] as? ItemStack ?: throw TypeCastException("value is not an itemstack")
    }

    override fun toArray(): Array<Any?> {
        return allValues.toTypedArray()
    }

    override fun checkTable(p0: Int): MutableMap<Any?, Any?> {
        return allValues[p0] as? MutableMap<Any?,Any?> ?: throw TypeCastException("value is not a table")
    }

    override fun isItemStack(p0: Int): Boolean {
        return if(p0 < count()) false else allValues[p0] is ItemStack
    }

    override fun isBoolean(p0: Int): Boolean {
        return if(p0 < count()) false else allValues[p0] is Boolean
    }

    override fun checkByteArray(p0: Int): ByteArray {
        return allValues[p0] as? ByteArray ?: throw TypeCastException("value is not a byte array")
    }

    override fun isDouble(p0: Int): Boolean {
        return if(p0 < count()) false else allValues[p0] is Double
    }

    override fun optInteger(p0: Int, p1: Int): Int {
        return if(p0 < count()) checkInteger(p0) else p1
    }

    override fun optByteArray(p0: Int, p1: ByteArray): ByteArray {
        return if(p0 < count()) checkByteArray(p0) else p1
    }

    override fun optString(p0: Int, p1: String): String {
        return if(p0 < count()) checkString(p0) else p1
    }

    override fun checkDouble(p0: Int): Double {
        return allValues[p0] as? Double ?: throw TypeCastException("value is not a double")
    }

    override fun checkBoolean(p0: Int): Boolean {
        return allValues[p0] as? Boolean ?: throw TypeCastException("value is not a boolean")
    }

    override fun isInteger(p0: Int): Boolean {
        return if(p0 < count()) false else allValues[p0] is Int
    }
}