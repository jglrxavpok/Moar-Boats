package org.jglrxavpok.moarboats.common.network

import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import java.lang.UnsupportedOperationException
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayList

interface MoarBoatsPacket {

    @Target(AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    /**
     * Allows a packet class to define fields that will not be serialized
     */
    annotation class Ignore

    @Target(AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    /**
     * Allows a packet class to define fields that will not be serialized
     */
    annotation class Nullable

    @Target(AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    /**
     * Allows a packet class to define fields that will not be serialized
     */
    annotation class ItemStackList

    companion object {
        /**
         * Internal cache to avoid performing a reflection lookup at each packet transmission
         */
        private val fieldCache = hashMapOf<Class<out MoarBoatsPacket>, List<Field>>()

        /**
         * Loads all fields from a packet class to prepare the cache
         */
        private fun enrichCache(withClass: Class<out MoarBoatsPacket>) {
            val cachedFields = mutableListOf<Field>()
            for(field in withClass.fields) {
                if(field.isAnnotationPresent(Ignore::class.java)) {
                    continue
                }
                cachedFields += field
            }
        }

        /**
         * Serializes a single field from a packet to a buffer
         */
        private fun serialize(packet: MoarBoatsPacket, field: Field, buffer: PacketBuffer) {
            if(field.isAnnotationPresent(Nullable::class.java)) {
                val present = field[packet] != null
                buffer.writeBoolean(present)
                if( ! present) {
                    return
                }
            }

            if(field.isAnnotationPresent(ItemStackList::class.java)) {
                val list = field[packet] as List<ItemStack>
                buffer.writeInt(list.size)
                val nbt = NBTTagCompound()
                val tmpList = NonNullList.from(ItemStack.EMPTY, *list.toTypedArray())
                ItemStackHelper.saveAllItems(nbt, tmpList)
                buffer.writeCompoundTag(nbt)
                return
            }
            write(field[packet], buffer)
        }

        private fun <T: Any> write(value: T, buffer: PacketBuffer) {
            when(value.javaClass) {
                Int::class.java, java.lang.Integer::class.java, Integer.TYPE -> buffer.writeInt(value as Int)
                Long::class.java, java.lang.Long::class.java, java.lang.Long.TYPE -> buffer.writeLong(value as Long)
                Boolean::class.java, java.lang.Boolean::class.java, java.lang.Boolean.TYPE -> buffer.writeBoolean(value as Boolean)
                Char::class.java, java.lang.Character::class.java, Character.TYPE -> buffer.writeChar((value as Char).toInt())
                Byte::class.java, java.lang.Byte::class.java, java.lang.Byte.TYPE -> buffer.writeByte((value as Byte).toInt())
                Short::class.java, java.lang.Short::class.java, java.lang.Short.TYPE -> buffer.writeShort((value as Short).toInt())
                Double::class.java, java.lang.Double::class.java, java.lang.Double.TYPE -> buffer.writeDouble(value as Double)
                Float::class.java, java.lang.Float::class.java, java.lang.Float.TYPE -> buffer.writeFloat(value as Float)

                ArrayList::class.java, LinkedList::class.java, List::class.java, MutableList::class.java -> {
                    val list = value as List<out Any>
                    buffer.writeInt(list.size)
                    if(list.isNotEmpty()) {
                        buffer.writeString(list[0].javaClass.canonicalName) // used to know the type when loading
                        list.forEach { elem ->
                            write(elem, buffer)
                        }
                    }
                }

                NBTTagList::class.java -> {
                    val container = NBTTagCompound()
                    val list = value as NBTTagList
                    container.putInt("nbt_type", list.tagType)
                    container.put("_", list)
                    buffer.writeCompoundTag(container)
                }

                NBTTagCompound::class.java -> {
                    buffer.writeCompoundTag(value as NBTTagCompound)
                }

                ResourceLocation::class.java -> {
                    val path = (value as ResourceLocation).path
                    buffer.writeString(path)
                }

                EnumFacing::class.java -> {
                    buffer.writeInt((value as EnumFacing).ordinal)
                }

                IntArray::class.java -> {
                    val array = value as IntArray
                    buffer.writeInt(array.size)
                    for(i in 0 until array.size) {
                        buffer.writeInt(array[i])
                    }
                }

                // Moar Boats special types
                ItemGoldenTicket.WaypointData::class.java -> {
                    val data = (value as ItemGoldenTicket.WaypointData)
                    buffer.writeString(data.uuid)
                    val nbt = data.write(NBTTagCompound())
                    buffer.writeCompoundTag(nbt)
                }

                LoopingOptions::class.java -> {
                    val option = value as LoopingOptions
                    buffer.writeInt(option.ordinal)
                }

                else -> throw UnsupportedOperationException("I don't know how to deal with type ${value.javaClass.canonicalName}")
            }
        }

        /**
         * Deserializes a single field from a buffer to a packet
         */
        private fun deserialize(packet: MoarBoatsPacket, field: Field, buffer: PacketBuffer) {
            if(field.isAnnotationPresent(Nullable::class.java)) {
                val present = buffer.readBoolean()
                if(present) {
                    field.set(packet, null)
                    return
                }
            }

            if(field.isAnnotationPresent(ItemStackList::class.java)) {
                val size = buffer.readInt()
                val tmpList = NonNullList.withSize(size, ItemStack.EMPTY)
                val nbt = buffer.readCompoundTag()!!
                ItemStackHelper.loadAllItems(nbt, tmpList)
                tmpList.addAll(tmpList)
                field.set(packet, mutableListOf<ItemStack>().apply { addAll(tmpList) })
                return
            }
            field.set(packet, read(field.type, buffer))
        }

        @Suppress("IMPLICIT_CAST_TO_ANY")
        private fun <T: Any> read(type: Class<T>, buffer: PacketBuffer): T {
            return when(type) {
                Int::class.java, java.lang.Integer::class.java, Integer.TYPE -> buffer.readInt()
                Long::class.java, java.lang.Long::class.java, java.lang.Long.TYPE -> buffer.readLong()
                Boolean::class.java, java.lang.Boolean::class.java, java.lang.Boolean.TYPE -> buffer.readBoolean()
                Char::class.java, java.lang.Character::class.java, Character.TYPE -> buffer.readChar()
                Byte::class.java, java.lang.Byte::class.java, java.lang.Byte.TYPE -> buffer.readByte()
                Short::class.java, java.lang.Short::class.java, java.lang.Short.TYPE -> buffer.readShort()
                Double::class.java, java.lang.Double::class.java, java.lang.Double.TYPE -> buffer.readDouble()
                Float::class.java, java.lang.Float::class.java, java.lang.Float.TYPE -> buffer.readFloat()

                ArrayList::class.java, List::class.java, MutableList::class.java -> {
                    mutableListOf<T>().apply {
                        val size = buffer.readInt()
                        if(size > 0) {
                            val clazzName = Class.forName(buffer.readString(200))
                            for(i in 0 until size) {
                                this += read(clazzName, buffer) as T
                            }
                        }
                    }
                }

                NBTTagList::class.java -> {
                    val container = buffer.readCompoundTag()!!
                    val type = container.getInt("nbt_type")
                    container.getList("_", type)
                }

                NBTTagCompound::class.java -> {
                    buffer.readCompoundTag()!!
                }

                ResourceLocation::class.java -> {
                    ResourceLocation(buffer.readString(200))
                }

                EnumFacing::class.java -> {
                    EnumFacing.values()[buffer.readInt() % EnumFacing.values().size]
                }

                IntArray::class.java -> {
                    val size = buffer.readInt()
                    val array = IntArray(size) {
                        buffer.readInt()
                    }
                }

                // Moar Boats special types
                ItemGoldenTicket.WaypointData::class.java -> {
                    val uuid = buffer.readString(100)
                    val nbt = buffer.readCompoundTag()!!
                    ItemGoldenTicket.WaypointData(uuid).apply { read(nbt) }
                }

                LoopingOptions::class.java -> {
                    val index = buffer.readInt()
                    LoopingOptions.values()[index % LoopingOptions.values().size]
                }

                else -> throw UnsupportedOperationException("I don't know how to deal with type ${type.canonicalName}")
            } as T
        }
    }

    fun encode(to: PacketBuffer) {
        if(javaClass !in fieldCache) {
           enrichCache(javaClass)
        }
        with(to) {
            fieldCache[this@MoarBoatsPacket.javaClass]?.forEach {
                serialize(this@MoarBoatsPacket, it, to)
            }
        }
    }

    fun decode(from: PacketBuffer): MoarBoatsPacket {
        if(javaClass !in fieldCache) {
            enrichCache(javaClass)
        }
        with(from) {
            fieldCache[this@MoarBoatsPacket.javaClass]?.forEach {
                deserialize(this@MoarBoatsPacket, it, from)
            }
        }
        return this
    }
}