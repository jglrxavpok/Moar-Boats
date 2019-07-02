package org.jglrxavpok.moarboats.common.state

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

abstract class BoatProperty<Type>(val module: BoatModule, val id: String) {

    abstract val type: Class<out Type>
    abstract val readProperty: NBTTagCompound.(String) -> Type
    abstract val writeProperty: NBTTagCompound.(String, Type) -> Unit

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
    override val readProperty = NBTTagCompound::getBoolean
    override val writeProperty = NBTTagCompound::setBoolean
}

class DoubleBoatProperty(module: BoatModule, id: String): BoatProperty<Double>(module, id) {
    override val type = java.lang.Double.TYPE
    override val readProperty = NBTTagCompound::getDouble
    override val writeProperty = NBTTagCompound::setDouble
}

class FloatBoatProperty(module: BoatModule, id: String): BoatProperty<Float>(module, id) {
    override val type = java.lang.Float.TYPE
    override val readProperty = NBTTagCompound::getFloat
    override val writeProperty = NBTTagCompound::setFloat
}

class IntBoatProperty(module: BoatModule, id: String): BoatProperty<Int>(module, id) {
    override val type = java.lang.Integer.TYPE
    override val readProperty = NBTTagCompound::getInt
    override val writeProperty = NBTTagCompound::setInt
}

class StringBoatProperty(module: BoatModule, id: String): BoatProperty<String>(module, id) {
    override val type = String::class.java
    override val readProperty = { compound: NBTTagCompound, id: String -> compound.getString(id) }
    override val writeProperty = NBTTagCompound::setString
}

class NBTListBoatProperty(module: BoatModule, id: String, val elementType: Int): BoatProperty<NBTTagList>(module, id) {
    override val type = NBTTagList::class.java

    override val readProperty: NBTTagCompound.(String) -> NBTTagList = { id -> this.getList(id, elementType) }
    override val writeProperty: NBTTagCompound.(String, NBTTagList) -> Unit = { id, list -> this.setTag(id, list) }
}

class ArrayBoatProperty<T: Any>(module: BoatModule, id: String, val array: Array<T>): BoatProperty<T>(module, id) {
    override val type = array[0].javaClass
    override val readProperty: NBTTagCompound.(String) -> T = { id -> array[this.getInt(id)] }
    override val writeProperty: NBTTagCompound.(String, T) -> Unit = { id, value -> setInt(id, array.indexOf(value))}
}

/**
 * Please release the positions after using them
 */
class BlockPosProperty(module: BoatModule, id: String): BoatProperty<BlockPos.PooledMutableBlockPos>(module, id) {
    override val type: Class<out BlockPos.PooledMutableBlockPos> = BlockPos.PooledMutableBlockPos::class.java
    override val readProperty: NBTTagCompound.(String) -> BlockPos.PooledMutableBlockPos = { id ->
        val pos = BlockPos.PooledMutableBlockPos.retain()
        pos.setPos(this.getInt(id+"_X"), this.getInt(id+"_Y"), this.getInt(id+"_Z"))
        pos
    }
    override val writeProperty: NBTTagCompound.(String, BlockPos.PooledMutableBlockPos) -> Unit = { id, pos ->
        this.setInt(id+"_X", pos.x)
        this.setInt(id+"_Y", pos.y)
        this.setInt(id+"_Z", pos.z)
    }
}

object EmptyMapData : MapData("empty")

class MapDataProperty(module: BoatModule, id: String): BoatProperty<MapData>(module, id) {
    override val type = MapData::class.java

    override val readProperty: NBTTagCompound.(String) -> MapData = { id ->
        if(!this.hasKey("mapData"))
            EmptyMapData
        else {
            val name = this.getString("mapName")
            val data = this.getCompound("mapData")
            MapData(name).apply { read(data) }
        }
    }

    override val writeProperty: NBTTagCompound.(String, MapData) -> Unit = { id, mapData ->
        setString("mapName", mapData.name)
        val mapDataNBT = mapData.write(NBTTagCompound())
        setTag("mapData", mapDataNBT)
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
