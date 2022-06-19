package org.jglrxavpok.moarboats.api

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.registries.*
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiModuleBase
import org.jglrxavpok.moarboats.common.MBBlocks
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.Modules
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory
import java.util.*
import java.util.function.Supplier

abstract class BoatModule {

    val menuType: MenuType<ContainerBoatModule<*>> = IForgeMenuType.create { windowId, inv, data ->
        val player = inv.player
        val boatID = data.readInt()
        val boat = player.level.getEntity(boatID) as ModularBoatEntity
        createContainer(windowId, player, boat)
    }

    abstract val id: ResourceLocation
    abstract val usesInventory: Boolean
    abstract val moduleSpot: Spot
    abstract fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean
    abstract fun controlBoat(from: IControllable)
    abstract fun update(from: IControllable)
    abstract fun onAddition(to: IControllable)
    open fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? = EmptyModuleContainer(menuType as MenuType<EmptyModuleContainer>, containerID, player.inventory, boat)

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
    fun guiFactory(): MenuScreens.ScreenConstructor<ContainerBoatModule<*>, GuiModuleBase<ContainerBoatModule<*>>> {
        return MenuScreens.ScreenConstructor { container, playerInv, title ->
            return@ScreenConstructor this.createGui(container.containerID, playerInv.player, container.boat) as GuiModuleBase<ContainerBoatModule<*>>
        }
    }
}

class BoatModuleEntry(val correspondingItem: Item, val module: BoatModule, val inventoryFactory: ((IControllable, BoatModule) -> BoatModuleInventory)?, val restriction: () -> Boolean)

object BoatModuleRegistry {

    lateinit var Registry: Supplier<IForgeRegistry<BoatModuleEntry>>

    operator fun get(location: ResourceLocation) = Registry.get().getValue(location) ?: error("No module with ID $location")

    fun findModule(heldItem: ItemStack): ResourceLocation? {
        for((key, entry) in Registry.get().entries) {
            if(entry.correspondingItem == heldItem.item)
                return key.location()
        }
        return null
    }

    fun findEntry(module: BoatModule): BoatModuleEntry? {
        return Registry.get().values.find { it.module == module}
    }

}

fun RegisterEvent.RegisterHelper<BoatModuleEntry>.registerModule(correspondingItem: Supplier<out Item>, module: BoatModule, inventoryFactory: ((IControllable, BoatModule) -> BoatModuleInventory)? = null, restriction: (() -> Boolean)? = null) {
    register(module.id, BoatModuleEntry(correspondingItem.get(), module, inventoryFactory, restriction ?: {true}))
    MoarBoats.logger.info("Registered module with ID ${module.id}")
    if(module.usesInventory && inventoryFactory == null)
        error("Module $module uses an inventory but no inventory factory was provided!")
}

fun RegisterEvent.RegisterHelper<BoatModuleEntry>.registerModule(module: BoatModule, correspondingItem: Supplier<out Item>, inventoryFactory: ((IControllable, BoatModule) -> BoatModuleInventory)? = null, restriction: (() -> Boolean)? = null) {
    registerModule(correspondingItem, module, inventoryFactory, restriction)
}