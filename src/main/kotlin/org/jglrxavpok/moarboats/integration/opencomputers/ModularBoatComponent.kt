package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.machine.Arguments
import li.cil.oc.api.machine.Callback
import li.cil.oc.api.machine.Context
import li.cil.oc.api.network.Visibility
import li.cil.oc.api.prefab.AbstractManagedEnvironment

class ModularBoatComponent(val host: BoatMachineHost): AbstractManagedEnvironment(), DeviceInfo {
    val node = li.cil.oc.api.Network.newNode(this, Visibility.Network)
            .withComponent("modularboat", Visibility.Neighbors)
            .withConnector()
            .create()

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

    @Callback()
    fun test(ctx: Context, args: Arguments): Array<Any?> {
        println("Hello from OC! ${args.toArray().joinToString(", ")}")
        return arrayOf(null, "ok")
    }
}
