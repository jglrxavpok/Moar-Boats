package org.jglrxavpok.moarboats.common

import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.*

object MBBlocks {
    @JvmField
    val Registry = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            MoarBoats.ModID
    )

    @JvmField
    val WaterborneConductor = Registry.register("waterborne_conductor", ::BlockWaterborneConductor)
    @JvmField
    val WaterborneComparator = Registry.register("waterborne_comparator", ::BlockWaterborneComparator)
    @JvmField
    val BoatBattery = Registry.register("boat_battery", ::BlockBoatBattery)
    @JvmField
    val BoatTank = Registry.register("boat_tank", ::BlockBoatTank)
    @JvmField
    val EnergyLoader = Registry.register("energy_loader", ::BlockEnergyLoader)
    @JvmField
    val EnergyUnloader = Registry.register("energy_unloader", ::BlockEnergyUnloader)
    @JvmField
    val CargoStopper = Registry.register("cargo_stopper", ::BlockCargoStopper)
    @JvmField
    val FluidLoader = Registry.register("fluid_loader", ::BlockFluidLoader)
    @JvmField
    val FluidUnloader = Registry.register("fluid_unloader", ::BlockFluidUnloader)
    @JvmField
    val MappingTable = Registry.register("mapping_table", ::BlockMappingTable)
}