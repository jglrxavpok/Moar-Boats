package org.jglrxavpok.moarboats.common.state

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

abstract class BoatProperty<Type>(val module: BoatModule, val id: String) {

    abstract val type: Class<Type>
    abstract val readProperty: NBTTagCompound.(String) -> Type
    abstract val writeProperty: NBTTagCompound.(String, Type) -> Unit

    operator fun get(boat: IControllable) = boat.getState(module).readProperty(id)
    operator fun set(boat: IControllable, value: Type) {
        val state = boat.getState(module)
        state.writeProperty(id, value)
        boat.saveState(module)
    }
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
    override val readProperty = NBTTagCompound::getInteger
    override val writeProperty = NBTTagCompound::setInteger
}

class NBTListBoatProperty(module: BoatModule, id: String, val elementType: Int): BoatProperty<NBTTagList>(module, id) {
    override val type = NBTTagList::class.java

    override val readProperty: NBTTagCompound.(String) -> NBTTagList = { id -> this.getTagList(id, elementType) }
    override val writeProperty: NBTTagCompound.(String, NBTTagList) -> Unit = { id, list -> this.setTag(id, list) }
}

fun BoatModule.IntBoatProperty(id: String) = IntBoatProperty(this, id)
fun BoatModule.BooleanBoatProperty(id: String) = BooleanBoatProperty(this, id)
fun BoatModule.FloatBoatProperty(id: String) = FloatBoatProperty(this, id)
fun BoatModule.DoubleBoatProperty(id: String) = DoubleBoatProperty(this, id)
fun BoatModule.NBTListBoatProperty(id: String, elementType: Int) = NBTListBoatProperty(this, id, elementType)