package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

object ContainerTypes {
    @JvmField
    val Registry = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MoarBoats.ModID)

    @JvmField
    val Empty = Registry.register("empty") {
        IForgeMenuType.create { windowId, inv, data ->
            return@create EmptyContainer(windowId, inv)
        } as MenuType<EmptyContainer>
    }

    @JvmField
    val MappingTable = Registry.register("mapping_table") {
        IForgeMenuType.create { windowId, inv, data ->
            val player = inv.player
            val pos = data.readBlockPos()
            val te = player.level.getBlockEntity(pos)
            te?.let {
                return@create ContainerMappingTable(windowId, it as TileEntityMappingTable, inv)
            }
        } as MenuType<ContainerMappingTable>
    }

    @JvmField
    val FluidLoader = Registry.register("fluid_loader") {
        IForgeMenuType.create { windowId, inv, data ->
            val player = inv.player
            val pos = data.readBlockPos()
            val te = player.level.getBlockEntity(pos) as? TileEntityListenable
            te?.let {
                return@create FluidContainer(true, windowId, te, te.getCapability(
                    CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseThrow(::NullPointerException), player)
            }
        } as MenuType<FluidContainer>
    }

    @JvmField
    val FluidUnloader = Registry.register("fluid_unloader") {
        IForgeMenuType.create { windowId, inv, data ->
            val player = inv.player
            val pos = data.readBlockPos()
            val te = player.level.getBlockEntity(pos) as? TileEntityListenable
            te?.let {
                return@create FluidContainer(false, windowId, te, te.getCapability(
                    CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseThrow(::NullPointerException), player)
            }
        } as MenuType<FluidContainer>
    }

    @JvmField
    val EnergyCharger = Registry.register("energy_loader") {
        IForgeMenuType.create { windowId, inv, data ->
            val player = inv.player
            val pos = data.readBlockPos()
            val te = player.level.getBlockEntity(pos) as? TileEntityEnergy
            te?.let {
                return@create EnergyContainer(true, windowId, te, player)
            }
        } as MenuType<EnergyContainer>
    }

    @JvmField
    val EnergyDischarger = Registry.register("energy_unloader") {
        IForgeMenuType.create { windowId, inv, data ->
            val player = inv.player
            val pos = data.readBlockPos()
            val te = player.level.getBlockEntity(pos) as? TileEntityEnergy
            te?.let {
                return@create EnergyContainer(false, windowId, te, player)
            }
        } as MenuType<EnergyContainer>
    }

    private fun <C: AbstractContainerMenu, B: UtilityBoatEntity<*, C>> registerUtilityContainer(id: String) = Registry.register("${id}_boat") {
        IForgeMenuType.create { windowId, inv, data ->
            val player = inv.player
            val entityID = data.readInt()
            val te = player.level.getEntity(entityID) as? B
            te?.let {
                return@create te.createMenu(windowId, player.inventory, player)
            }
        } as MenuType<C>
    }

    @JvmField
    val FurnaceBoat: RegistryObject<MenuType<UtilityFurnaceContainer>> = registerUtilityContainer("furnace")

    @JvmField
    val SmokerBoat: RegistryObject<MenuType<UtilitySmokerContainer>> = registerUtilityContainer("smoker")

    @JvmField
    val BlastFurnaceBoat: RegistryObject<MenuType<UtilityBlastFurnaceContainer>> = registerUtilityContainer("blast_furnace")

    @JvmField
    val CraftingBoat: RegistryObject<MenuType<UtilityWorkbenchContainer>> = registerUtilityContainer("workbench")

    @JvmField
    val GrindstoneBoat: RegistryObject<MenuType<UtilityGrindstoneContainer>> = registerUtilityContainer("grindstone")

    @JvmField
    val LoomBoat: RegistryObject<MenuType<UtilityLoomContainer>> = registerUtilityContainer("loom")

    @JvmField
    val CartographyTableBoat: RegistryObject<MenuType<UtilityCartographyTableContainer>> = registerUtilityContainer("cartography_table")

    @JvmField
    val StonecutterBoat: RegistryObject<MenuType<UtilityStonecutterContainer>> = registerUtilityContainer("stonecutter")

    @JvmField
    val ChestBoat: RegistryObject<MenuType<UtilityChestContainer>> = registerUtilityContainer("chest")

    @JvmField
    val EnderChestBoat: RegistryObject<MenuType<ChestMenu>> = registerUtilityContainer("ender_chest")

    @JvmField
    val ShulkerBoat: RegistryObject<MenuType<UtilityShulkerContainer>> = registerUtilityContainer("shulker")
}