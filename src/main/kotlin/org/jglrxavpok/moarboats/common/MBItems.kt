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

    @JvmField
    val Registry = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            MoarBoats.ModID
    )

    private val defaultItemBlockProperties = Item.Properties().tab(MoarBoats.MainCreativeTab)
    @JvmField
    val BoatBattery = Registry.register("boat_battery") { BlockItem(MBBlocks.BoatBattery.get(), defaultItemBlockProperties) }
    @JvmField
    val BoatTank = Registry.register("boat_tank") { BlockItem(MBBlocks.BoatTank.get(), defaultItemBlockProperties) }
    @JvmField
    val EnergyLoader = Registry.register("energy_loader") { BlockItem(MBBlocks.EnergyLoader.get(), defaultItemBlockProperties) }
    @JvmField
    val EnergyUnloader = Registry.register("energy_unloader") { BlockItem(MBBlocks.EnergyUnloader.get(), defaultItemBlockProperties) }
    @JvmField
    val FluidLoader = Registry.register("fluid_loader") { BlockItem(MBBlocks.FluidLoader.get(), defaultItemBlockProperties) }
    @JvmField
    val FluidUnloader = Registry.register("fluid_unloader") { BlockItem(MBBlocks.FluidUnloader.get(), defaultItemBlockProperties) }
    @JvmField
    val MappingTable = Registry.register("mapping_table") { BlockItem(MBBlocks.MappingTable.get(), defaultItemBlockProperties) }

    @JvmField
    val AnimalBoat = Registry.register("animal_boat") { AnimalBoatItem() }
    @JvmField
    val SeatItem = Registry.register("seat") { SeatItem() }
    @JvmField
    val HelmItem = Registry.register("helm") { HelmItem() }
    @JvmField
    val RudderItem = Registry.register("rudder") { RudderItem() }
    @JvmField
    val IceBreakerItem = Registry.register("icebreaker") { IceBreakerItem() }
    @JvmField
    val RopeItem = Registry.register("rope") { RopeItem() }
    @JvmField
    val DivingBottleItem = Registry.register("diving_bottle") { DivingBottleItem() }
    @JvmField
    val WaterborneConductorItem = Registry.register("waterborne_redstone") { WaterborneConductorItem() }
    @JvmField
    val WaterborneComparatorItem = Registry.register("waterborne_comparator") { WaterborneComparatorItem() }
    @JvmField
    val CreativeEngineItem = Registry.register("creative_engine") { CreativeEngineItem() }
    @JvmField
    val ItemGoldenTicket = Registry.register("golden_ticket") { ItemGoldenTicket() }
    @JvmField
    val MapItemWithPath = Registry.register("map_with_path") { MapItemWithPath() }
    @JvmField
    val ChunkLoaderItem = Registry.register("chunk_loader") { ChunkLoaderItem() }
    @JvmField
    val OarsItem = Registry.register("oars") { OarsItem() }
    @JvmField
    val CargoStopperItem = Registry.register("cargo_stopper") { CargoStopperItem() }

    @JvmField
    val ModularBoats = mapOf(*DyeColor.values().map { color -> color to Registry.register("modular_boat_${color.getName()}") { ModularBoatItem(color) } }.toTypedArray())

    @JvmField
    val BlastFurnaceBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_blast_furnace_boat") { BlastFurnaceBoatItem(type) } }.toTypedArray())
    @JvmField
    val CartographyTableBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_cartography_table_boat") { CartographyTableBoatItem(type) } }.toTypedArray())
    @JvmField
    val CraftingTableBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_crafting_table_boat") { CraftingTableBoatItem(type) } }.toTypedArray())
    @JvmField
    val EnderChestBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_ender_chest_boat") { EnderChestBoatItem(type) } }.toTypedArray())
    @JvmField
    val FurnaceBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_furnace_boat") { FurnaceBoatItem(type) } }.toTypedArray())
    @JvmField
    val GrindstoneBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_grindstone_boat") { GrindstoneBoatItem(type) } }.toTypedArray())
    @JvmField
    val JukeboxBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_jukebox_boat") { JukeboxBoatItem(type) } }.toTypedArray())
    @JvmField
    val LoomBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_loom_boat") { LoomBoatItem(type) } }.toTypedArray())
    @JvmField
    val ShulkerBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_shulker_boat") { ShulkerBoatItem(type) } }.toTypedArray())
    @JvmField
    val SmokerBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_smoker_boat") { SmokerBoatItem(type) } }.toTypedArray())
    @JvmField
    val StonecutterBoats = mapOf(*BoatType.values().map { type -> type to Registry.register("${type.getFullName()}_stonecutter_boat") { StonecutterBoatItem(type) } }.toTypedArray())
}