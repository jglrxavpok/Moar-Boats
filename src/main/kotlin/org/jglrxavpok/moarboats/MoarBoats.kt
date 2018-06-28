package org.jglrxavpok.moarboats

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items as MCItems
import net.minecraft.init.Blocks as MCBlocks
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.item.ItemBlock
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.items.*
import org.jglrxavpok.moarboats.common.modules.*


@Mod(modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", modid = MoarBoats.ModID, dependencies = "required-after:forgelin;",
        name = "Moar Boats", version = "2.1.0.1", updateJSON = "https://raw.githubusercontent.com/jglrxavpok/Moar-Boats/master/updateCheck.json")
object MoarBoats {
    const val ModID = "moarboats"

    lateinit var logger: Logger

    @SidedProxy(clientSide = "org.jglrxavpok.moarboats.client.Proxy", serverSide = "org.jglrxavpok.moarboats.server.Proxy")
    lateinit var proxy: MoarBoatsProxy

    val network = SimpleNetworkWrapper(ModID)
    lateinit var config: Configuration
        private set

    val CreativeTab = object: CreativeTabs("moarboats") {
        override fun getTabIconItem(): ItemStack {
            return ItemStack(ModularBoatItem)
        }

    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        config = Configuration(event.suggestedConfigurationFile)
        MBConfig.backing = config
        MBConfig.loadAll()
        BoatModuleRegistry.registerModule(ResourceLocation("moarboats:furnace_engine"), Item.getItemFromBlock(MCBlocks.FURNACE), FurnaceEngineModule, { boat, module -> EngineModuleInventory(boat, module) })
        BoatModuleRegistry.registerModule(ResourceLocation("moarboats:chest"), Item.getItemFromBlock(MCBlocks.CHEST), ChestModule, { boat, module -> ChestModuleInventory(boat, module) })
        BoatModuleRegistry.registerModule(ResourceLocation("moarboats:helm"), HelmItem, HelmModule, { boat, module -> SimpleModuleInventory(1, "helm", boat, module) })
        BoatModuleRegistry.registerModule(ResourceLocation("moarboats:fishing"), MCItems.FISHING_ROD, FishingModule, { boat, module -> SimpleModuleInventory(1, "fishing", boat, module) })
        BoatModuleRegistry.registerModule(SeatModule, SeatItem)
        BoatModuleRegistry.registerModule(AnchorModule, Item.getItemFromBlock(MCBlocks.ANVIL))
        BoatModuleRegistry.registerModule(SolarEngineModule, Item.getItemFromBlock(MCBlocks.DAYLIGHT_DETECTOR))
        BoatModuleRegistry.registerModule(CreativeEngineModule, CreativeEngineItem)
        BoatModuleRegistry.registerModule(IceBreakerModule, IceBreakerItem)
        BoatModuleRegistry.registerModule(SonarModule, Item.getItemFromBlock(MCBlocks.NOTEBLOCK))
        BoatModuleRegistry.registerModule(DispenserModule, Item.getItemFromBlock(MCBlocks.DISPENSER), { boat, module -> SimpleModuleInventory(3*5, "dispenser", boat, module) })
        BoatModuleRegistry.registerModule(DivingModule, DivingBottleItem)
        BoatModuleRegistry.registerModule(RudderModule, RudderItem)
        BoatModuleRegistry.registerModule(DropperModule, Item.getItemFromBlock(MCBlocks.DROPPER), { boat, module -> SimpleModuleInventory(3*5, "dropper", boat, module) })
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(ItemEventHandler)
        proxy.preInit()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init()
        DataSerializers.registerSerializer(ResourceLocationsSerializer)
        DataSerializers.registerSerializer(UniqueIDSerializer)
    }

    @SubscribeEvent
    fun registerBlocks(e: RegistryEvent.Register<Block>) {
        e.registry.registerAll(*Blocks.list.toTypedArray())
    }

    @SubscribeEvent
    fun registerItems(e: RegistryEvent.Register<Item>) {
        e.registry.registerAll(*Items.list.toTypedArray())
        for (block in Blocks.list) {
            e.registry.register(ItemBlock(block).setRegistryName(block.registryName).setUnlocalizedName(block.unlocalizedName))
        }
    }

    @SubscribeEvent
    fun registerEntities(e: RegistryEvent.Register<EntityEntry>) {
        e.registry.registerAll(*EntityEntries.list.toTypedArray())
    }
}