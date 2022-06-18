package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType

object ContainerTypes {
    @JvmStatic
    lateinit var Empty: MenuType<EmptyContainer>

    @JvmStatic
    lateinit var MappingTable: MenuType<ContainerMappingTable>

    @JvmStatic
    lateinit var FluidLoader: MenuType<FluidContainer>

    @JvmStatic
    lateinit var FluidUnloader: MenuType<FluidContainer>

    @JvmStatic
    lateinit var EnergyCharger: MenuType<EnergyContainer>

    @JvmStatic
    lateinit var EnergyDischarger: MenuType<EnergyContainer>

    @JvmStatic
    lateinit var FurnaceBoat: MenuType<UtilityFurnaceContainer>

    @JvmStatic
    lateinit var SmokerBoat: MenuType<UtilitySmokerContainer>

    @JvmStatic
    lateinit var BlastFurnaceBoat: MenuType<UtilityBlastFurnaceContainer>

    @JvmStatic
    lateinit var CraftingBoat: MenuType<UtilityWorkbenchContainer>

    @JvmStatic
    lateinit var GrindstoneBoat: MenuType<UtilityGrindstoneContainer>

    @JvmStatic
    lateinit var LoomBoat: MenuType<UtilityLoomContainer>

    @JvmStatic
    lateinit var CartographyTableBoat: MenuType<UtilityCartographyTableContainer>

    @JvmStatic
    lateinit var StonecutterBoat: MenuType<UtilityStonecutterContainer>

    @JvmStatic
    lateinit var ChestBoat: MenuType<UtilityChestContainer>

    @JvmStatic
    lateinit var EnderChestBoat: MenuType<ChestMenu>

    @JvmStatic
    lateinit var ShulkerBoat: MenuType<UtilityShulkerContainer>
}