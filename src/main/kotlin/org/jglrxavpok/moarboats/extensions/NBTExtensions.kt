package org.jglrxavpok.moarboats.extensions

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagList

fun NBTTagList.swap(index1: Int, index2: Int) {
    if(index1 !in 0 until tagCount())
        throw IndexOutOfBoundsException("index1: $index1, size is ${tagCount()}")
    if(index2 !in 0 until tagCount())
        throw IndexOutOfBoundsException("index2: $index2, size is ${tagCount()}")

    /* Explanation:
        Copy all tags and swap 1&2
     */
    val tags = (0 until tagCount()).map { this[it] }.toList()
    removeAll { true }
    tags.mapIndexed { index, nbt ->
        when(index) {
            index1 -> tags[index2]
            index2 -> tags[index1]
            else -> nbt
        }
    }.forEach(this::appendTag)
}

fun NBTTagList.insert(index: Int, tag: NBTBase) {
    /* Explanation:
        Remove all tags until the index provided, re-add the tag before the one to insert, add the tag to insert, add the other tags
     */
    val tags = mutableListOf<NBTBase>()
    repeat(this.tagCount()-index) {
        tags += this.removeTag(index)
    }
    tags.firstOrNull()?.let { this.appendTag(it) }
    this.appendTag(tag)
    if(tags.size > 1)
        tags.drop(1).forEach(this::appendTag)
}