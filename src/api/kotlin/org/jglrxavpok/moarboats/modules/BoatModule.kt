package org.jglrxavpok.moarboats.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

abstract class BoatModule {

    abstract val id: ResourceLocation
    abstract val usesInventory: Boolean
    abstract val moduleType: Type
    abstract fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean
    abstract fun controlBoat(from: IControllable)
    abstract fun update(from: IControllable)
    abstract fun onAddition(to: IControllable)
    abstract fun createContainer(player: EntityPlayer, boat: IControllable): Container

    @SideOnly(Side.CLIENT)
    abstract fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen

    open fun onInit(to: IControllable) {
        rng.setSeed(to.rngSeed)
    }

    val rng = Random()

    protected fun IControllable.saveState() = this.saveState(this@BoatModule)
    protected fun IControllable.getState() = this.getState(this@BoatModule)
    protected fun IControllable.getInventory() = this.getInventory(this@BoatModule)

    enum class Type {
        Engine,
        Storage,
        Misc
    }

    open fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {}
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

    fun findEntry(module: BoatModule): BoatModuleEntry? {
        return backingMap.values.find { it.module == module }
    }

}