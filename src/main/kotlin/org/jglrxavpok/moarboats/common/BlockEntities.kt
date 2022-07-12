package org.jglrxavpok.moarboats.common

import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.extensions.IForgeBlockEntity
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.tileentity.*

object BlockEntities {

    @JvmField
    val Registry = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MoarBoats.ModID)

    @JvmField
    val FluidLoader = Registry.register("fluid_loader") {
        BlockEntityType.Builder.of(::TileEntityFluidLoader, MBBlocks.FluidLoader.get())
            .build(null /* no data fixer */)
    }

    @JvmField
    val FluidUnloader = Registry.register("fluid_unloader") {
        BlockEntityType.Builder.of(::TileEntityFluidUnloader, MBBlocks.FluidUnloader.get())
            .build(null /* no data fixer */)
    }

    @JvmField
    val EnergyLoader = Registry.register("energy_loader") {
        BlockEntityType.Builder.of(::TileEntityEnergyLoader, MBBlocks.EnergyLoader.get())
            .build(null /* no data fixer */)
    }

    @JvmField
    val EnergyUnloader = Registry.register("energy_unloader") {
        BlockEntityType.Builder.of(::TileEntityEnergyUnloader, MBBlocks.EnergyUnloader.get())
            .build(null /* no data fixer */)
    }

    @JvmField
    val MappingTable = Registry.register("mapping_table") {
        BlockEntityType.Builder.of(::TileEntityMappingTable, MBBlocks.MappingTable.get())
            .build(null /* no data fixer */)
    }

}