package org.jglrxavpok.moarboats.api

import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.registries.IForgeRegistry
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiModuleBase
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import java.util.*

abstract class BoatModule {

    val containerType by lazy {
        IForgeContainerType.create { windowId, inv, data ->
            val player = inv.player
            val boatID = data.readInt()
            val boat = player.level.getEntity(boatID) as ModularBoatEntity
            boat?.let {
                return@create createContainer(windowId, player as ClientPlayerEntity, it)
            } ?: return@create null
        }.setRegistryName(id) as MenuType<ContainerBoatModule<*>>
    }

    abstract val id: ResourceLocation
    abstract val usesInventory: Boolean
    abstract val moduleSpot: Spot
    abstract fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean
    abstract fun controlBoat(from: IControllable)
    abstract fun update(from: IControllable)
    abstract fun onAddition(to: IControllable)
    abstract fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>?

    /**
     * Set to false if you want the menu to be displayed at the bottom of the module tabs (no config modules use this)
     */
    open val isMenuInteresting: Boolean = true

    /**
     * Priority for using a hopper: the higher, the strongest priority. Use 0 to disallow hopper interactions
     * eg. Chests have 20, furnace engines and helms have 0
     */
    open val hopperPriority = 1

    @OnlyIn(Dist.CLIENT)
    abstract fun createGui(containerID: Int, player: Player, boat: IControllable): Screen

    open fun onInit(to: IControllable, fromItem: ItemStack?) {}

    /**
     * Reads additional information from the boat entity NBT data. No need to read/store module state created via the BoatProperty objects
     */
    open fun readFromNBT(boat: IControllable, compound: CompoundTag) {}

    /**
     * Writes additional information to the boat entity NBT data. No need to read/store module state created via the BoatProperty objects
     */
    open fun writeToNBT(boat: IControllable, compound: CompoundTag) = compound

    val rng = Random()

    protected fun IControllable.saveState() = this.saveState(this@BoatModule)
    protected fun IControllable.getState() = this.getState(this@BoatModule)
    protected fun IControllable.getInventory() = this.getInventory(this@BoatModule)

    enum class Spot(val id: String) {
        Engine("engine"),
        Storage("storage"),
        Navigation("navigation"),
        Misc("misc");

        val text = Component.translatable("general.spot.$id")
    }

    open fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {}

    fun generateInteractionObject(boat: IControllable): MenuProvider {
        return BoatModuleInteractionObject(this, boat)
    }

    @OnlyIn(Dist.CLIENT)
    fun guiFactory(): ScreenManager.IScreenFactory<ContainerBoatModule<*>, GuiModuleBase<ContainerBoatModule<*>>> {
        return ScreenManager.IScreenFactory { container, playerInv, title ->
            return@IScreenFactory this.createGui(container.containerID, playerInv.player, container.boat) as GuiModuleBase<ContainerBoatModule<*>>
        }
    }

}

class BoatModuleEntry(val correspondingItem: Item, val module: BoatModule, val inventoryFactory: ((IControllable, BoatModule) -> BoatModuleInventory)?, val restriction: () -> Boolean): ForgeRegistryEntry<BoatModuleEntry>()

object BoatModuleRegistry {

    lateinit var forgeRegistry: IForgeRegistry<BoatModuleEntry>

    operator fun get(location: ResourceLocation) = forgeRegistry.getValue(location) ?: error("No module with ID $location")

    fun findModule(heldItem: ItemStack): ResourceLocation? {
        for((key, entry) in forgeRegistry.entries) {
            if(entry.correspondingItem == heldItem.item)
                return key.location()
        }
        return null
    }

    fun findEntry(module: BoatModule): BoatModuleEntry? {
        return forgeRegistry.values.find {it.module == module}
    }

}

fun IForgeRegistry<BoatModuleEntry>.registerModule(module: BoatModule, correspondingItem: Item, inventoryFactory: ((IControllable, BoatModule) -> BoatModuleInventory)? = null, restriction: (() -> Boolean)? = null) {
    registerModule(module.id, correspondingItem, module, inventoryFactory, restriction)
}

fun IForgeRegistry<BoatModuleEntry>.registerModule(name: ResourceLocation, correspondingItem: Item, module: BoatModule, inventoryFactory: ((IControllable, BoatModule) -> BoatModuleInventory)? = null, restriction: (() -> Boolean)? = null) {
    val entry = BoatModuleEntry(correspondingItem, module, inventoryFactory, restriction ?: {true})
    entry.registryName = module.id
    if(!MinecraftForge.EVENT_BUS.post(ModuleRegistryEvent(entry))) {
        this.register(entry)
        MoarBoats.logger.info("Registered module with ID $name")
        if(module.usesInventory && inventoryFactory == null)
            error("Module $module uses an inventory but no inventory factory was provided!")
    }
}
