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
    val Registry = DeferredRegister.create(ForgeRegistries.CONTAINERS, MoarBoats.ModID)

    private fun makeMenu(module: BoatModule): MenuType<*> {
        return IForgeMenuType.create { windowId, inv, data ->
            val player = inv.player
            val boatID = data.readInt()
            val boat = player.level.getEntity(boatID) as ModularBoatEntity
            module.createContainer(windowId, player, boat)
        }
    }

    val ChestModuleMenu = Registry.register("chest_module") { makeMenu(ChestModule) as MenuType<ContainerChestModule> }
    val DispenserModuleMenu = Registry.register("dispenser_module") { makeMenu(DispenserModule) as MenuType<ContainerDispenserModule> }
    val FishingModuleMenu = Registry.register("dispenser_module") { makeMenu(FishingModule) as MenuType<ContainerFishingModule> }
    val FurnaceModuleMenu = Registry.register("dispenser_module") { makeMenu(FurnaceEngineModule) as MenuType<ContainerFurnaceEngine> }
    val HelmModuleMenu = Registry.register("dispenser_module") { makeMenu(HelmModule) as MenuType<ContainerHelmModule> }

    val EmptyModuleMenu = Registry.register("empty_menu_module") {
        IForgeMenuType.create { windowId, inv, data ->
            val player = inv.player
            val boatID = data.readInt()
            val boat = player.level.getEntity(boatID) as ModularBoatEntity
            EmptyModuleContainer(windowId, inv, boat)
        }
    }

    val Empty = Registry.register("empty") {
        IForgeMenuType.create { windowId, inv, data ->
            return@create EmptyContainer(windowId, inv)
        } as MenuType<EmptyContainer>
    }

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

    val FurnaceBoat: RegistryObject<MenuType<UtilityFurnaceContainer>> = registerUtilityContainer("furnace")

    val SmokerBoat: RegistryObject<MenuType<UtilitySmokerContainer>> = registerUtilityContainer("smoker")

    val BlastFurnaceBoat: RegistryObject<MenuType<UtilityBlastFurnaceContainer>> = registerUtilityContainer("blast_furnace")

    val CraftingBoat: RegistryObject<MenuType<UtilityWorkbenchContainer>> = registerUtilityContainer("workbench")

    val GrindstoneBoat: RegistryObject<MenuType<UtilityGrindstoneContainer>> = registerUtilityContainer("grindstone")

    val LoomBoat: RegistryObject<MenuType<UtilityLoomContainer>> = registerUtilityContainer("loom")

    val CartographyTableBoat: RegistryObject<MenuType<UtilityCartographyTableContainer>> = registerUtilityContainer("cartography_table")

    val StonecutterBoat: RegistryObject<MenuType<UtilityStonecutterContainer>> = registerUtilityContainer("stonecutter")

    val ChestBoat: RegistryObject<MenuType<UtilityChestContainer>> = registerUtilityContainer("chest")

    val EnderChestBoat: RegistryObject<MenuType<ChestMenu>> = registerUtilityContainer("ender_chest")

    val ShulkerBoat: RegistryObject<MenuType<UtilityShulkerContainer>> = registerUtilityContainer("shulker")
}