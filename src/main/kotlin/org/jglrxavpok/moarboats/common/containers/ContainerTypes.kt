package org.jglrxavpok.moarboats.common.containers

import net.minecraft.inventory.container.*
import net.minecraftforge.registries.ObjectHolder
import org.jglrxavpok.moarboats.MoarBoats

object ContainerTypes {
    @JvmStatic
    lateinit var Empty: ContainerType<EmptyContainer>

    @JvmStatic
    lateinit var MappingTable: ContainerType<ContainerMappingTable>

    @JvmStatic
    lateinit var FluidLoader: ContainerType<FluidContainer>

    @JvmStatic
    lateinit var FluidUnloader: ContainerType<FluidContainer>

    @JvmStatic
    lateinit var EnergyCharger: ContainerType<EnergyContainer>

    @JvmStatic
    lateinit var EnergyDischarger: ContainerType<EnergyContainer>

    @JvmStatic
    lateinit var FurnaceBoat: ContainerType<UtilityFurnaceContainer>

    @JvmStatic
    lateinit var SmokerBoat: ContainerType<UtilitySmokerContainer>

    @JvmStatic
    lateinit var BlastFurnaceBoat: ContainerType<UtilityBlastFurnaceContainer>

    @JvmStatic
    lateinit var CraftingBoat: ContainerType<UtilityWorkbenchContainer>

    @JvmStatic
    lateinit var GrindstoneBoat: ContainerType<UtilityGrindstoneContainer>

    @JvmStatic
    lateinit var LoomBoat: ContainerType<UtilityLoomContainer>

    @JvmStatic
    lateinit var CartographyTableBoat: ContainerType<UtilityCartographyTableContainer>

    @JvmStatic
    lateinit var StonecutterBoat: ContainerType<UtilityStonecutterContainer>

    @JvmStatic
    lateinit var ChestBoat: ContainerType<UtilityChestContainer>

    @JvmStatic
    lateinit var EnderChestBoat: ContainerType<ChestContainer>

    @JvmStatic
    lateinit var ShulkerBoat: ContainerType<UtilityShulkerContainer>
}