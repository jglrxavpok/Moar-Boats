package org.jglrxavpok.moarboats.common.state

import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

abstract class BoatProperty<Type>(val module: BoatModule, val id: String) {

    abstract val type: Class<out Type>
    abstract val readProperty: CompoundTag.(String) -> Type
    abstract val writeProperty: CompoundTag.(String, Type) -> Unit

    var isLocal = false
        private set

    operator fun get(boat: IControllable) = boat.getState(module).readProperty(id)
    operator fun set(boat: IControllable, value: Type) {
        val state = boat.getState(module, isLocal)
        state.writeProperty(id, value)
        boat.saveState(module, isLocal)
    }

    fun makeLocal() = this.apply { isLocal = true }
}

class BooleanBoatProperty(module: BoatModule, id: String): BoatProperty<Boolean>(module, id) {
    override val type = java.lang.Boolean.TYPE
    override val readProperty = CompoundTag::getBoolean
    override val writeProperty = CompoundTag::putBoolean
}

class DoubleBoatProperty(module: BoatModule, id: String): BoatProperty<Double>(module, id) {
    override val type = java.lang.Double.TYPE
    override val readProperty = CompoundTag::getDouble
    override val writeProperty = CompoundTag::putDouble
}

class FloatBoatProperty(module: BoatModule, id: String): BoatProperty<Float>(module, id) {
    override val type = java.lang.Float.TYPE
    override val readProperty = CompoundTag::getFloat
    override val writeProperty = CompoundTag::putFloat
}

class IntBoatProperty(module: BoatModule, id: String): BoatProperty<Int>(module, id) {
    override val type = java.lang.Integer.TYPE
    override val readProperty = CompoundTag::getInt
    override val writeProperty = CompoundTag::putInt
}

class StringBoatProperty(module: BoatModule, id: String): BoatProperty<String>(module, id) {
    override val type = String::class.java
    override val readProperty = { compound: CompoundTag, id: String -> compound.getString(id) }
    override val writeProperty = CompoundTag::putString
}

class NBTListBoatProperty(module: BoatModule, id: String, val elementType: Int): BoatProperty<ListTag>(module, id) {
    override val type = ListTag::class.java

    override val readProperty: CompoundTag.(String) -> ListTag = { id -> this.getList(id, elementType) }
    override val writeProperty: CompoundTag.(String, ListTag) -> Unit = { id, list -> this.put(id, list) }
}

class ArrayBoatProperty<T: Any>(module: BoatModule, id: String, val array: Array<T>): BoatProperty<T>(module, id) {
    override val type = array[0].javaClass
    override val readProperty: CompoundTag.(String) -> T = { id -> array[this.getInt(id)] }
    override val writeProperty: CompoundTag.(String, T) -> Unit = { id, value -> putInt(id, array.indexOf(value))}
}

/**
 * Please release the positions after using them
 */
class BlockPosProperty(module: BoatModule, id: String): BoatProperty<MutableBlockPos>(module, id) {
    private val pos = MutableBlockPos()

    override val type: Class<out MutableBlockPos> = MutableBlockPos::class.java
    override val readProperty: CompoundTag.(String) -> MutableBlockPos = { id ->
        pos.set(this.getInt(id+"_X"), this.getInt(id+"_Y"), this.getInt(id+"_Z"))
        pos
    }
    override val writeProperty: CompoundTag.(String, MutableBlockPos) -> Unit = { id, pos ->
        this.putInt(id+"_X", pos.x)
        this.putInt(id+"_Y", pos.y)
        this.putInt(id+"_Z", pos.z)
    }
}

class MapDataProperty(module: BoatModule, id: String): BoatProperty<MapItemSavedData?>(module, id) {
    override val type = MapItemSavedData::class.java

    override val readProperty: CompoundTag.(String) -> MapItemSavedData? = { id ->
        if(!this.contains("mapData"))
            null
        else {
            val data = this.getCompound("mapData")
            MapItemSavedData.load(data)
        }
    }

    override val writeProperty: CompoundTag.(String, MapItemSavedData?) -> Unit = { id, mapData ->
        if(mapData != null) {
            put("mapData", mapData.save(CompoundTag()))
        }
    }
}

fun BoatModule.IntBoatProperty(id: String) = IntBoatProperty(this, id)
fun BoatModule.BooleanBoatProperty(id: String) = BooleanBoatProperty(this, id)
fun BoatModule.FloatBoatProperty(id: String) = FloatBoatProperty(this, id)
fun BoatModule.DoubleBoatProperty(id: String) = DoubleBoatProperty(this, id)
fun BoatModule.StringBoatProperty(id: String) = StringBoatProperty(this, id)
fun BoatModule.BlockPosProperty(id: String) = BlockPosProperty(this, id)
fun BoatModule.NBTListBoatProperty(id: String, elementType: Int) = NBTListBoatProperty(this, id, elementType)
fun <T: Any> BoatModule.ArrayBoatProperty(id: String, array: Array<T>) = ArrayBoatProperty(this, id, array)
fun BoatModule.MapDataProperty(id: String) = MapDataProperty(this, id)
