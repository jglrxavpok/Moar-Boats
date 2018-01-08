package org.jglrxavpok.moarboats.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation

abstract class BoatModule {

    abstract val id: ResourceLocation
    abstract val usesInventory: Boolean
    abstract val moduleType: Type
    abstract fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean)
    abstract fun controlBoat(from: IControllable)
    abstract fun update(from: IControllable)
    abstract fun onAddition(to: IControllable)

    protected fun IControllable.saveState() = this.saveState(this@BoatModule)
    protected fun IControllable.getState() = this.getState(this@BoatModule)
    protected fun IControllable.getInventory() = this.getInventory(this@BoatModule)

    enum class Type {
        Engine,
        Storage,
        Misc
    }
}

data class BoatModuleEntry(val correspondingItem: Item, val module: BoatModule, val inventoryFactory: ((IControllable, BoatModule) -> IBoatModuleInventory)?)

object BoatModuleRegistry {

    private val backingMap = hashMapOf<ResourceLocation, BoatModuleEntry>()

    fun registerModule(name: ResourceLocation, correspondingItem: Item, module: BoatModule, inventoryFactory: ((IControllable, BoatModule) -> IBoatModuleInventory)?) {
        backingMap[name] = BoatModuleEntry(correspondingItem, module, inventoryFactory)
        if(module.usesInventory && inventoryFactory == null)
            error("Module $module uses an inventory but no inventory factory was provided!")
    }

    operator fun get(location: ResourceLocation) = backingMap[location] ?: error("No module with ID $location")

    fun findModule(heldItem: ItemStack): ResourceLocation? {
        for((key, entry) in backingMap) {
            if(entry.correspondingItem == heldItem.item)
                return key
        }
        return null
    }

}