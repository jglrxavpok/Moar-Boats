package org.jglrxavpok.moarboats.common

import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.registries.DeferredRegister
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.registerModule
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory

object Modules {

    val Registry = DeferredRegister.create(BoatModuleRegistry.Registry.get(), MoarBoats.ModID)

    val ChestModuleEntry = Registry.registerModule("chest", {Blocks.CHEST.asItem()}, ChestModule, ::ChestModuleInventory)
    val FurnaceEngineModuleEntry = Registry.registerModule("furnace_engine", {Blocks.FURNACE.asItem()}, FurnaceEngineModule, ::EngineModuleInventory)
    val HelmModuleEntry = Registry.registerModule("helm", MBItems.HelmItem, HelmModule, { boat, module -> SimpleModuleInventory(1, "helm", boat, module) })
    val FishingModuleEntry = Registry.registerModule("fishing", { Items.FISHING_ROD}, FishingModule, { boat, module -> SimpleModuleInventory(1, "fishing", boat, module) })
    val SeatModuleEntry = Registry.registerModule(SeatModule, MBItems.SeatItem)
    val AnchorModuleEntry = Registry.registerModule(AnchorModule, {Blocks.ANVIL.asItem()})
    val SolarEngineModuleEntry = Registry.registerModule(SolarEngineModule, {Blocks.DAYLIGHT_DETECTOR.asItem()})
    val CreativeEngineModuleEntry = Registry.registerModule(CreativeEngineModule, MBItems.CreativeEngineItem)
    val IceBreakerModuleEntry = Registry.registerModule(IceBreakerModule, MBItems.IceBreakerItem)
    val SonarModuleEntry = Registry.registerModule(SonarModule, {Blocks.NOTE_BLOCK.asItem()})
    val DispenserModuleEntry = Registry.registerModule(DispenserModule, {Blocks.DISPENSER.asItem()}, { boat, module -> SimpleModuleInventory(3 * 5, "dispenser", boat, module) })
    val DivingModuleEntry = Registry.registerModule(DivingModule, MBItems.DivingBottleItem)
    val RudderModuleEntry = Registry.registerModule(RudderModule, MBItems.RudderItem)
    val DropperModuleEntry = Registry.registerModule(DropperModule, {Blocks.DROPPER.asItem()}, { boat, module -> SimpleModuleInventory(3 * 5, "dropper", boat, module) })
    val BatteryModuleEntry = Registry.registerModule(BatteryModule, MBItems.BoatBattery)
    val FluidTankModuleEntry = Registry.registerModule(FluidTankModule, MBItems.BoatTank)
    val ChunkLoadingModuleEntry = Registry.registerModule(ChunkLoadingModule, MBItems.ChunkLoaderItem, restriction = MoarBoatsConfig.chunkLoader.allowed::get)
    val OarEngineModuleEntry = Registry.registerModule(OarEngineModule, MBItems.OarsItem)
}