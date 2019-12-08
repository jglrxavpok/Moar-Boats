package org.jglrxavpok.moarboats.extensions

import net.minecraft.nbt.INBT
import net.minecraft.nbt.ListNBT

fun ListNBT.swap(index1: Int, index2: Int) {
    if(index1 !in 0 until size)
        throw IndexOutOfBoundsException("index1: $index1, size is $size")
    if(index2 !in 0 until size)
        throw IndexOutOfBoundsException("index2: $index2, size is $size")

    /* Explanation:
        Copy all tags and swap 1&2
     */
    val tags = (0 until size).map { this[it] }.toList()
    clear()
    this += tags.mapIndexed { index, nbt ->
        when(index) {
            index1 -> tags[index2]
            index2 -> tags[index1]
            else -> nbt
        }
    }
}

fun ListNBT.insert(index: Int, tag: INBT) {
    /* Explanation:
        Remove all tags until the index provided, re-add the tag before the one to insert, add the tag to insert, add the other tags
     */
    val tags = mutableListOf<INBT>()
    repeat(this.size-index) {
        tags += this.removeAt(index)
    }
    tags.firstOrNull()?.let { this += it }
    this.add(tag)
    if(tags.size > 1)
        this += tags.drop(1)
}