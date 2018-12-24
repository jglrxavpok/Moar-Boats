package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.machine.Arguments
import li.cil.oc.api.machine.Callback
import li.cil.oc.api.machine.Context
import li.cil.oc.api.machine.Value
import li.cil.oc.api.network.Visibility
import li.cil.oc.api.prefab.AbstractManagedEnvironment
import li.cil.oc.api.prefab.AbstractValue
import net.minecraft.nbt.NBTTagCompound
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.NoBlockReason

class ModularBoatComponent(val host: BoatMachineHost): AbstractManagedEnvironment(), DeviceInfo, Value {
    val node = li.cil.oc.api.Network.newNode(this, Visibility.Network)
            .withComponent("modularboat", Visibility.Neighbors)
            .withConnector()
            .create()?.let { it }
    val boat = host.boat

    val deviceInfoMap = hashMapOf(
            DeviceInfo.DeviceAttribute.Class to DeviceInfo.DeviceClass.System,
            DeviceInfo.DeviceAttribute.Description to "Modular Boat",
            DeviceInfo.DeviceAttribute.Capacity to "0",
            DeviceInfo.DeviceAttribute.Serial to "BMcBF01",
            DeviceInfo.DeviceAttribute.Version to "1.0.0",
            DeviceInfo.DeviceAttribute.Product to "Modular Boats Series",
            DeviceInfo.DeviceAttribute.Vendor to "JGLR Technologies"
    )
    init {
        setNode(node)
    }

    override fun getDeviceInfo(): MutableMap<String, String> {
        return deviceInfoMap
    }

    override fun canUpdate(): Boolean {
        return true
    }

    override fun unapply(p0: Context?, p1: Arguments?) {}

    override fun call(p0: Context?, p1: Arguments?) = throw UnsupportedOperationException("Object is not callable")

    override fun apply(p0: Context?, p1: Arguments?) = throw UnsupportedOperationException("Object is not callable")

    override fun dispose(p0: Context?) {}

    @Callback(direct = true)
    fun getModules(ctx: Context, args: Arguments): OCResult {
        return result(host.boat.modules.map { Pair(it.moduleSpot.id, ModuleValue(it)) })
    }

    @Callback(direct = true)
    fun getModule(ctx: Context, args: Arguments): OCResult {
        val module = host.boat.modules.firstOrNull { it.moduleSpot.id == args.checkString(0) } ?: return result(null, "No module in spot")
        return result(ModuleValue(module))
    }

    @Callback(direct = true)
    fun getVelocityVector(ctx: Context, args: Arguments): OCResult {
        return result(boat.velocityX, boat.velocityY, boat.velocityZ)
    }

    @Callback(direct = true)
    fun getPositionVector(ctx: Context, args: Arguments): OCResult {
        return result(boat.positionX, boat.positionY, boat.positionZ)
    }

    @Callback(direct = true)
    fun getYaw(ctx: Context, args: Arguments): OCResult {
        return result(boat.yaw)
    }

    @Callback
    fun getOwnerUUID(ctx: Context, args: Arguments): OCResult {
        return result(boat.ownerUUID?.toString())
    }

    @Callback
    fun getOwnerName(ctx: Context, args: Arguments): OCResult {
        return result(boat.ownerName)
    }

    @Callback(direct = true)
    fun getColorIndex(ctx: Context, args: Arguments): OCResult {
        return result(boat.color.ordinal)
    }

    @Callback(direct = true)
    fun isBlocked(ctx: Context, args: Arguments): OCResult {
        return result(boat.blockedReason != NoBlockReason)
    }

    @Callback
    fun accelerate(ctx: Context, args: Arguments): OCResult {
        host.accelerationFactor = args.optDouble(0, 1.0).toFloat()
        return result()
    }

    @Callback
    fun decelerate(ctx: Context, args: Arguments): OCResult {
        host.decelerationFactor = args.optDouble(0, 1.0).toFloat()
        return result()
    }

    @Callback
    fun turnLeft(ctx: Context, args: Arguments): OCResult {
        host.turnLeftFactor = args.optDouble(0, 1.0).toFloat()
        return result()
    }

    @Callback
    fun turnRight(ctx: Context, args: Arguments): OCResult {
        host.turnRightFactor = args.optDouble(0, 1.0).toFloat()
        return result()
    }

    @Callback
    fun stopTurningLeft(ctx: Context, args: Arguments): OCResult {
        host.turnLeftFactor = null
        return result()
    }

    @Callback
    fun stopTurningRight(ctx: Context, args: Arguments): OCResult {
        host.turnRightFactor = null
        return result()
    }

    @Callback
    fun stopAccelerating(ctx: Context, args: Arguments): OCResult {
        host.accelerationFactor = null
        return result()
    }

    @Callback
    fun stopDecelerating(ctx: Context, args: Arguments): OCResult {
        host.decelerationFactor = null
        return result()
    }

    @Callback
    fun getBlockedReason(ctx: Context, args: Arguments): OCResult {
        return result(boat.blockedReason)
    }

    class ModuleValue(): AbstractValue() {
        @JvmField
        var id: String? = null

        @JvmField
        var spot: String? = null

        constructor(module: BoatModule): this() {
            id = module.id.toString()
            spot = module.moduleSpot.id
        }

        override fun save(nbt: NBTTagCompound) {
            id?.let {
                nbt.setString("id", it)
            }
            spot?.let {
                nbt.setString("spot", it)
            }
        }

        override fun load(nbt: NBTTagCompound) {
            id = nbt.getString("id")
            spot = nbt.getString("spot")
        }
    }
}
