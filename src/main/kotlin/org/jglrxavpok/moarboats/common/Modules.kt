package org.jglrxavpok.moarboats.common

import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.registerModule
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory

@Mod.EventBusSubscriber(modid = MoarBoats.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
object Modules {

    val RegistryKey = ResourceKey.createRegistryKey<BoatModuleEntry>(ResourceLocation(MoarBoats.ModID, "modules"))

    @SubscribeEvent
    fun register(event: RegisterEvent) {
        event.register(RegistryKey) { helper ->
            helper.registerModule({Blocks.FURNACE.asItem()}, FurnaceEngineModule, ::EngineModuleInventory)
            helper.registerModule(SolarEngineModule, {Blocks.DAYLIGHT_DETECTOR.asItem()})
            helper.registerModule(CreativeEngineModule, MBItems.CreativeEngineItem)
            helper.registerModule(OarEngineModule, MBItems.OarsItem)

            helper.registerModule({Blocks.CHEST.asItem()}, ChestModule, ::ChestModuleInventory)
            helper.registerModule(DispenserModule, {Blocks.DISPENSER.asItem()}, { boat, module -> SimpleModuleInventory(3 * 5, "dispenser", boat, module) })
            helper.registerModule(DropperModule, {Blocks.DROPPER.asItem()}, { boat, module -> SimpleModuleInventory(3 * 5, "dropper", boat, module) })
            helper.registerModule(BatteryModule, MBItems.BoatBattery)
            helper.registerModule(FluidTankModule, MBItems.BoatTank)
            helper.registerModule(SeatModule, MBItems.SeatItem)

            helper.registerModule(MBItems.HelmItem, HelmModule, { boat, module -> SimpleModuleInventory(1, "helm", boat, module) })
            helper.registerModule(SonarModule, {Blocks.NOTE_BLOCK.asItem()})
            helper.registerModule(RudderModule, MBItems.RudderItem)

            helper.registerModule({ Items.FISHING_ROD}, FishingModule, { boat, module -> SimpleModuleInventory(1, "fishing", boat, module) })
            helper.registerModule(AnchorModule, {Blocks.ANVIL.asItem()})
            helper.registerModule(IceBreakerModule, MBItems.IceBreakerItem)
            helper.registerModule(DivingModule, MBItems.DivingBottleItem)
            helper.registerModule(ChunkLoadingModule, MBItems.ChunkLoaderItem, restriction = MoarBoatsConfig.chunkLoader.allowed::get)
        }

        event.register(ForgeRegistries.MENU_TYPES.registryKey) { helper ->
            val modules = listOf(
                ChestModule,
                FurnaceEngineModule,
                HelmModule,
                FishingModule,
                SeatModule,
                AnchorModule,
                SolarEngineModule,
                CreativeEngineModule,
                IceBreakerModule,
                SonarModule,
                DispenserModule,
                DivingModule,
                RudderModule,
                DropperModule,
                BatteryModule,
                FluidTankModule,
                ChunkLoadingModule,
                OarEngineModule,
            )

            for(m in modules) {
                helper.register(m.id, m.menuType)
            }
        }
    }
}