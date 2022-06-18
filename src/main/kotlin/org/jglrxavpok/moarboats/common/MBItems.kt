package org.jglrxavpok.moarboats.common

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.items.*

object MBItems {

    val Registry = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            MoarBoats.ModID
    )

    private val defaultItemBlockProperties = Item.Properties().tab(MoarBoats.MainCreativeTab)
    val BoatBattery = Registry.register("boat_battery") { BlockItem(MBBlocks.BoatBattery.get(), defaultItemBlockProperties) }
    val BoatTank = Registry.register("boat_tank") { BlockItem(MBBlocks.BoatTank.get(), defaultItemBlockProperties) }
    val EnergyLoader = Registry.register("energy_loader") { BlockItem(MBBlocks.EnergyLoader.get(), defaultItemBlockProperties) }
    val EnergyUnloader = Registry.register("energy_unloader") { BlockItem(MBBlocks.EnergyUnloader.get(), defaultItemBlockProperties) }
    val FluidLoader = Registry.register("fluid_loader") { BlockItem(MBBlocks.FluidLoader.get(), defaultItemBlockProperties) }
    val FluidUnloader = Registry.register("fluid_unloader") { BlockItem(MBBlocks.FluidUnloader.get(), defaultItemBlockProperties) }
    val MappingTable = Registry.register("mapping_table") { BlockItem(MBBlocks.MappingTable.get(), defaultItemBlockProperties) }

    val AnimalBoat = Registry.register("animal_boat") { AnimalBoatItem() }

    val AnimalBoatItem = Registry.register("animal_boat") { AnimalBoatItem() }
    val SeatItem = Registry.register("seat") { SeatItem() }
    val HelmItem = Registry.register("helm") { HelmItem() }
    val RudderItem = Registry.register("rudder") { RudderItem() }
    val IceBreakerItem = Registry.register("ice_breaker") { IceBreakerItem() }
    val RopeItem = Registry.register("rope") { RopeItem() }
    val DivingBottleItem = Registry.register("diving_bottle") { DivingBottleItem() }
    val WaterborneConductorItem = Registry.register("waterborne_conductor") { WaterborneConductorItem() }
    val WaterborneComparatorItem = Registry.register("waterborne_comparator") { WaterborneComparatorItem() }
    val CreativeEngineItem = Registry.register("creative_engine") { CreativeEngineItem() }
    val ItemGoldenTicket = Registry.register("item_golden_ticket") { ItemGoldenTicket() }
    val MapItemWithPath = Registry.register("map_with_path") { MapItemWithPath() }
    val ChunkLoaderItem = Registry.register("chunk_loader") { ChunkLoaderItem() }
    val OarsItem = Registry.register("oars") { OarsItem() }
    val CargoStopperItem = Registry.register("cargo_stopper") { CargoStopperItem() }

    val ModularBoats = mapOf(*DyeColor.values().map { color -> color to Registry.register("modular_boat_${color.getName()}") { ModularBoatItem(color) } }.toTypedArray())

    val BlastFurnaceBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_blast_furnace_boat") { BlastFurnaceBoatItem(type) } }.toTypedArray())
    val CartographyTableBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_cartography_table_boat") { CartographyTableBoatItem(type) } }.toTypedArray())
    val ChestBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_chest_boat") { ChestBoatItem(type) } }.toTypedArray())
    val CraftingTableBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_crafting_table_boat") { CraftingTableBoatItem(type) } }.toTypedArray())
    val EnderChestBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_ender_chest_boat") { EnderChestBoatItem(type) } }.toTypedArray())
    val FurnaceBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_furnace_boat") { FurnaceBoatItem(type) } }.toTypedArray())
    val GrindstoneBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_grindstone_boat") { GrindstoneBoatItem(type) } }.toTypedArray())
    val JukeboxBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_jukebox_boat") { JukeboxBoatItem(type) } }.toTypedArray())
    val LoomBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_loom_boat") { LoomBoatItem(type) } }.toTypedArray())
    val ShulkerBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_shulker_boat") { ShulkerBoatItem(type) } }.toTypedArray())
    val SmokerBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_smoker_boat") { SmokerBoatItem(type) } }.toTypedArray())
    val StonecutterBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_stonecutter_boat") { StonecutterBoatItem(type) } }.toTypedArray())
}