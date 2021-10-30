package org.jglrxavpok.moarboats.common.network

import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.MusicDiscItem
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.network.PacketBuffer
import net.minecraft.util.*
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.GameData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

interface MoarBoatsPacket {

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    /**
     * Allows a packet class to define fields that will not be serialized
     */
    annotation class Ignore

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    /**
     * Allows a packet class to define fields that will not be serialized
     */
    annotation class Nullable

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    /**
     * Allows a packet class to define fields that will not be serialized
     */
    annotation class ItemStackList

    companion object {
        /**
         * Internal cache to avoid performing a reflection lookup at each packet transmission
         */
        private val fieldCache = hashMapOf<KClass<out MoarBoatsPacket>, List<KMutableProperty<*>>>()

        /**
         * Loads all fields from a packet class to prepare the cache
         */
        private fun enrichCache(withClass: KClass<out MoarBoatsPacket>) {
            val cachedFields = mutableListOf<KMutableProperty<*>>()
            for(field in withClass.memberProperties.filterIsInstance<KMutableProperty<*>>()) {
                if(field.annotations.any { it.annotationClass == Ignore::class }) {
                    continue
                }
                MoarBoats.logger.debug("Enriching field cache for packet $withClass: ${field.name} (type: ${field.returnType})")
                cachedFields += field
            }
            fieldCache[withClass] = cachedFields
        }

        /**
         * Serializes a single field from a packet to a buffer
         */
        private fun serialize(packet: MoarBoatsPacket, field: KMutableProperty<*>, buffer: PacketBuffer) {
            if(field.javaField!!.annotations.any { it.annotationClass == Nullable::class } || field.returnType.isMarkedNullable) {
                val present = field.getter.call(packet) != null
                buffer.writeBoolean(present)
                if( ! present) {
                    return
                }
            }

            if(field.javaField!!.annotations.any { it.annotationClass == ItemStackList::class }) {
                val list = field.getter.call(packet) as List<ItemStack>
                buffer.writeInt(list.size)
                val nbt = CompoundNBT()
                val tmpList = NonNullList.of(ItemStack.EMPTY, *list.toTypedArray())
                ItemStackHelper.saveAllItems(nbt, tmpList)
                buffer.writeNbt(nbt)
                return
            }
            write(field[packet] as Any, buffer)
        }

        private fun <T: Any> write(value: T, buffer: PacketBuffer) {
            write(value, value.javaClass, buffer)
        }

        private fun <T: Any> write(value: T, type: Class<T>, buffer: PacketBuffer) {
            when(type) {
                Int::class.java, java.lang.Integer::class.java, Integer.TYPE -> buffer.writeInt(value as Int)
                Long::class.java, java.lang.Long::class.java, java.lang.Long.TYPE -> buffer.writeLong(value as Long)
                Boolean::class.java, java.lang.Boolean::class.java, java.lang.Boolean.TYPE -> buffer.writeBoolean(value as Boolean)
                Char::class.java, java.lang.Character::class.java, Character.TYPE -> buffer.writeChar((value as Char).toInt())
                Byte::class.java, java.lang.Byte::class.java, java.lang.Byte.TYPE -> buffer.writeByte((value as Byte).toInt())
                Short::class.java, java.lang.Short::class.java, java.lang.Short.TYPE -> buffer.writeShort((value as Short).toInt())
                Double::class.java, java.lang.Double::class.java, java.lang.Double.TYPE -> buffer.writeDouble(value as Double)
                Float::class.java, java.lang.Float::class.java, java.lang.Float.TYPE -> buffer.writeFloat(value as Float)
                String::class.java -> buffer.writeUtf(value as String)

                ArrayList::class.java, LinkedList::class.java, List::class.java, MutableList::class.java -> {
                    val list = value as List<out Any>
                    buffer.writeInt(list.size)
                    if(list.isNotEmpty()) {
                        buffer.writeUtf(list[0].javaClass.canonicalName) // used to know the type when loading
                        list.forEach { elem ->
                            write(elem, buffer)
                        }
                    }
                }

                IntArray::class.java -> {
                    val array = value as IntArray
                    buffer.writeInt(array.size)
                    for(i in 0 until array.size) {
                        buffer.writeInt(array[i])
                    }
                }

                // MC Types
                ListNBT::class.java -> {
                    val container = CompoundNBT()
                    val list = value as ListNBT
                    container.putInt("nbt_type", list.elementType.toInt())
                    container.put("_", list)
                    buffer.writeNbt(container)
                }

                CompoundNBT::class.java -> {
                    buffer.writeNbt(value as CompoundNBT)
                }

                ResourceLocation::class.java -> {
                    val path = (value as ResourceLocation).toString()
                    buffer.writeUtf(path)
                }

                Direction::class.java -> {
                    buffer.writeInt((value as Direction).ordinal)
                }

                SoundEvent::class.java -> {
                    buffer.writeUtf((value as SoundEvent).registryName.toString())
                }

                SoundCategory::class.java -> {
                    buffer.writeInt((value as SoundCategory).ordinal)
                }

                ItemStack::class.java -> {
                    val stack = value as ItemStack
                    val nbt = stack.save(CompoundNBT())
                    buffer.writeNbt(nbt)
                }

                MusicDiscItem::class.java -> {
                    buffer.writeResourceLocation((value as Item).registryName)
                }

                // Moar Boats special types
                ItemGoldenTicket.WaypointData::class.java -> {
                    val data = (value as ItemGoldenTicket.WaypointData)
                    buffer.writeUtf(data.uuid)
                    val nbt = data.write(CompoundNBT())
                    buffer.writeNbt(nbt)
                }

                LoopingOptions::class.java -> {
                    val option = value as LoopingOptions
                    buffer.writeInt(option.ordinal)
                }

                else -> {
                    if(type.superclass != null) {
                        try {
                            write(value, type.superclass as Class<Any>, buffer)
                        } catch (e: UnsupportedOperationException) {
                            throw UnsupportedOperationException("I don't know how to deal with type ${type.canonicalName}")
                        }
                    } else {
                        throw UnsupportedOperationException("I don't know how to deal with type ${type.canonicalName}")
                    }
                }
            }
        }

        /**
         * Deserializes a single field from a buffer to a packet
         */
        private fun deserialize(packet: MoarBoatsPacket, field: KMutableProperty<*>, buffer: PacketBuffer) {
            if(field.javaField!!.annotations.any { it.annotationClass == Nullable::class } || field.returnType.isMarkedNullable) {
                val present = buffer.readBoolean()
                if( ! present) {
                    field[packet] = null
                    return
                }
            }

            if(field.javaField!!.annotations.any { it.annotationClass == ItemStackList::class }) {
                val size = buffer.readInt()
                val tmpList = NonNullList.withSize(size, ItemStack.EMPTY)
                val nbt = buffer.readNbt()!!
                ItemStackHelper.loadAllItems(nbt, tmpList)
                field[packet] = mutableListOf<ItemStack>().apply { addAll(tmpList) }
                return
            }
            try {
                field[packet] = read(field.javaField!!.type, buffer)
            } catch (e: Exception) {
                MoarBoats.logger.error("Failed to decode ${field.name} of packet ${packet.javaClass.canonicalName}: ${e.message}, annotations are ${field.javaField!!.annotations.joinToString(", ") { it.javaClass.canonicalName }}")
                throw e
            }
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
                String::class.java -> buffer.readUtf(200)

                ArrayList::class.java, List::class.java, MutableList::class.java -> {
                    mutableListOf<T>().apply {
                        val size = buffer.readInt()
                        if(size > 0) {
                            val clazzName = Class.forName(buffer.readUtf(200))
                            for(i in 0 until size) {
                                this += read(clazzName, buffer) as T
                            }
                        }
                    }
                }

                ListNBT::class.java -> {
                    val container = buffer.readNbt()!!
                    val type = container.getInt("nbt_type")
                    container.getList("_", type)
                }

                IntArray::class.java -> {
                    val size = buffer.readInt()
                    IntArray(size) {
                        buffer.readInt()
                    }
                }

                // MC Types
                CompoundNBT::class.java -> {
                    buffer.readNbt()!!
                }

                ResourceLocation::class.java -> {
                    ResourceLocation(buffer.readUtf(200))
                }

                Direction::class.java -> {
                    Direction.values()[buffer.readInt() % Direction.values().size]
                }

                SoundEvent::class.java -> {
                    ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation(buffer.readUtf(200)))
                }

                SoundCategory::class.java -> {
                    SoundCategory.values()[buffer.readInt() % SoundCategory.values().size]
                }

                ItemStack::class.java -> {
                    val nbt = buffer.readNbt()
                    ItemStack.of(nbt)
                }

                Item::class.java -> {
                    ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation()) ?: Items.AIR
                }

                // Moar Boats special types
                ItemGoldenTicket.WaypointData::class.java -> {
                    val uuid = buffer.readUtf(100)
                    val nbt = buffer.readNbt()!!
                    ItemGoldenTicket.WaypointData(uuid).apply { read(nbt) }
                }

                LoopingOptions::class.java -> {
                    val index = buffer.readInt()
                    LoopingOptions.values()[index % LoopingOptions.values().size]
                }

                else -> {
                    if(type.superclass != null) {
                        try {
                            read(type.superclass as Class<Any>, buffer)
                        } catch (e: UnsupportedOperationException) {
                            throw UnsupportedOperationException("I don't know how to deal with type ${type.canonicalName}", e)
                        }
                    } else {
                        throw UnsupportedOperationException("I don't know how to deal with type ${type.canonicalName}")
                    }
                }
            } as T
        }

        private operator fun KMutableProperty<*>.get(packet: MoarBoatsPacket): Any? {
            this.isAccessible = true
            val p = this.getter.call(packet)
            this.isAccessible = false
            return p
        }

        private operator fun KMutableProperty<*>.set(packet: MoarBoatsPacket, value: Any?) {
            this.isAccessible = true
            val p = this.setter.call(packet, value)
            this.isAccessible = false
        }
    }

    fun encode(to: PacketBuffer) {
        try {
            if(this::class !in fieldCache) {
                enrichCache(this::class)
            }
            with(to) {
                fieldCache[this@MoarBoatsPacket::class]?.forEach {
                    serialize(this@MoarBoatsPacket, it, to)
                }
            }
        } catch (e: Exception) {
            MoarBoats.logger.error("Error while encoding packet ${this::class}", e)
        }
    }

    fun decode(from: PacketBuffer): MoarBoatsPacket {
        try {
            if(this::class !in fieldCache) {
                enrichCache(this::class)
            }
            with(from) {
                fieldCache[this@MoarBoatsPacket::class]?.forEach {
                    deserialize(this@MoarBoatsPacket, it, from)
                }
            }
        } catch (e: Exception) {
            MoarBoats.logger.error("Error while decoding packet ${this::class}", e)
        }
        return this
    }
}
