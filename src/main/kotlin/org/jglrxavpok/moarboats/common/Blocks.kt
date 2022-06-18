package org.jglrxavpok.moarboats.common

import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.*

object Blocks {
    val Registry = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            MoarBoats.ModID
    )

    /*
    TODO
    val WaterboneConductor = Registry.register("waterbone_conductor", ::BlockWaterborneConductor)
    val WaterborneComparator = Registry.register("waterbone_comparator", ::BlockWaterborneComparator)
    val BoatBattery = Registry.register("boat_battery", ::BlockBoatBattery)
    val BoatTank = Registry.register("boat_tank", ::BlockBoatTank)
    val EnergyLoader = Registry.register("energy_loader", ::BlockEnergyLoader)
    val EnergyUnloader = Registry.register("energy_unloader", ::BlockEnergyUnloader)
    val CargoStopper = Registry.register("cargo_stopper", ::BlockCargoStopper)
    val FluidLoader = Registry.register("fluid_loader", ::BlockFluidLoader)
    val FluidUnloader = Registry.register("fluid_unloader", ::BlockFluidUnloader)
    val MappingTable = Registry.register("mapping_table", ::BlockMappingTable)
*/
}