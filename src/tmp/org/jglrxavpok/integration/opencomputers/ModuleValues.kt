package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.machine.Arguments
import li.cil.oc.api.machine.Callback
import li.cil.oc.api.machine.Context
import li.cil.oc.api.prefab.AbstractValue
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.BaseEngineModule

open class ModuleValue(): AbstractValue() {
    private var id: String? = null
    private var spot: String? = null

    constructor(module: BoatModule): this() {
        id = module.id.toString()
        spot = module.moduleSpot.id
    }

    override fun save(nbt: CompoundNBT) {
        id?.let {
            nbt.setString("id", it)
        }
        spot?.let {
            nbt.setString("spot", it)
        }
    }

    override fun load(nbt: CompoundNBT) {
        id = nbt.getString("id")
        spot = nbt.getString("spot")
    }

    @Callback
    fun getID(ctx: Context, args: Arguments) = result(id)

    @Callback
    fun getSpot(ctx: Context, args: Arguments) = result(spot)
}

open class StorageModuleValue(val boat: IControllable, val module: BoatModule): ModuleValue(module) {
    @Callback
    fun getInventorimageHeight(ctx: Context, args: Arguments): OCResult {
        return result(boat.getInventory(module).sizeInventory)
    }

    @Callback
    fun getItem(ctx: Context, args: Arguments): OCResult {
        return result(boat.getInventory(module).getItem(args.checkInteger(0)))
    }
}

open class EngineModuleValue(val boat: IControllable, val mod: BoatModule): ModuleValue(mod) {
    val module = mod as BaseEngineModule

    @Callback(direct = true)
    fun hasFuel(ctx: Context, args: Arguments) = result(module.hasFuel(boat))

    @Callback(direct = true)
    fun getFuelTime(ctx: Context, args: Arguments) = result(module.getFuelTime(args.checkItemStack(0)))

    @Callback(direct = true)
    fun remainingTimeInTicks(ctx: Context, args: Arguments) = result(module.remainingTimeInTicks(boat))

    @Callback(direct = true)
    fun remainingTimeInPercent(ctx: Context, args: Arguments) = result(module.remainingTimeInPercent(boat))

    @Callback(direct = true)
    fun estimatedTotalTicks(ctx: Context, args: Arguments) = result(module.estimatedTotalTicks(boat))

}