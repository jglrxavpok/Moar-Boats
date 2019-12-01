package org.jglrxavpok.moarboats.common.containers

import net.minecraft.inventory.container.ContainerType
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
}