package org.jglrxavpok.moarboats

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items as MCItems
import net.minecraft.init.Blocks as MCBlocks
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.item.ItemBlock
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.MapStorage
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.registries.RegistryBuilder
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.registerModule
import org.jglrxavpok.moarboats.common.blocks.*
import org.jglrxavpok.moarboats.common.items.*
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.tileentity.*
import org.jglrxavpok.moarboats.integration.LoadIntegrationPlugins
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin

@Mod.EventBusSubscriber
@Mod(modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", modid = MoarBoats.ModID, dependencies = "required-after:forgelin;",
        name = "Moar Boats", version = "4.0.0.0b5", updateJSON = "https://raw.githubusercontent.com/jglrxavpok/Moar-Boats/master/updateCheck.json")
object MoarBoats {
    const val ModID = "moarboats"

    lateinit var logger: Logger

    @SidedProxy(clientSide = "org.jglrxavpok.moarboats.client.Proxy", serverSide = "org.jglrxavpok.moarboats.server.Proxy")
    lateinit var proxy: MoarBoatsProxy

    val network = SimpleNetworkWrapper(ModID)
    lateinit var config: Configuration
        private set

    val MachineMaterial = object: Material(MapColor.IRON) {
        init {
            setImmovableMobility()
        }
    }

    val CreativeTab = object: CreativeTabs("moarboats") {
        override fun getTabIconItem(): ItemStack {
            return ItemStack(ModularBoatItem)
        }

        override fun displayAllRelevantItems(itemList: NonNullList<ItemStack>) {
            for(item in Items.list) {
                item.getSubItems(this, itemList)
            }
            for(block in Blocks.list) {
                val itemBlock = Item.getItemFromBlock(block)
                if(itemBlock != MCItems.AIR) {
                    itemBlock.getSubItems(this, itemList)
                }
            }
        }
    }

    private lateinit var plugins: List<MoarBoatsPlugin>

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        config = Configuration(event.suggestedConfigurationFile)
        MBConfig.backing = config
        MBConfig.loadAll()
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(ItemEventHandler)
        proxy.preInit()
        ForgeChunkManager.setForcedChunkLoadingCallback(MoarBoats) { tickets, world ->
            for(ticket in tickets) {
                for(pos in ticket.chunkList) {
                    ForgeChunkManager.forceChunk(ticket, pos)
                }
            }
        }

        plugins = LoadIntegrationPlugins(event)
        plugins.forEach(MoarBoatsPlugin::preInit)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        plugins.forEach(MoarBoatsPlugin::postInit)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init()
        DataSerializers.registerSerializer(ResourceLocationsSerializer)
        DataSerializers.registerSerializer(UniqueIDSerializer)
        plugins.forEach(MoarBoatsPlugin::init)
    }

    @JvmStatic
    @SubscribeEvent
    fun createRegistry(e: RegistryEvent.NewRegistry) {
        BoatModuleRegistry.forgeRegistry = RegistryBuilder<BoatModuleEntry>()
                .allowModification()
                .setName(ResourceLocation(ModID, "module_registry"))
                .setType(BoatModuleEntry::class.java)
                .create()
    }

    @SubscribeEvent
    fun registerModules(event: RegistryEvent.Register<BoatModuleEntry>) {
        event.registry.registerModule(ResourceLocation("moarboats:furnace_engine"), Item.getItemFromBlock(MCBlocks.FURNACE), FurnaceEngineModule, { boat, module -> EngineModuleInventory(boat, module) })
        event.registry.registerModule(ResourceLocation("moarboats:chest"), Item.getItemFromBlock(MCBlocks.CHEST), ChestModule, { boat, module -> ChestModuleInventory(boat, module) })
        event.registry.registerModule(ResourceLocation("moarboats:helm"), HelmItem, HelmModule, { boat, module -> SimpleModuleInventory(1, "helm", boat, module) })
        event.registry.registerModule(ResourceLocation("moarboats:fishing"), MCItems.FISHING_ROD, FishingModule, { boat, module -> SimpleModuleInventory(1, "fishing", boat, module) })
        event.registry.registerModule(SeatModule, SeatItem)
        event.registry.registerModule(AnchorModule, Item.getItemFromBlock(MCBlocks.ANVIL))
        event.registry.registerModule(SolarEngineModule, Item.getItemFromBlock(MCBlocks.DAYLIGHT_DETECTOR))
        event.registry.registerModule(CreativeEngineModule, CreativeEngineItem)
        event.registry.registerModule(IceBreakerModule, IceBreakerItem)
        event.registry.registerModule(SonarModule, Item.getItemFromBlock(MCBlocks.NOTEBLOCK))
        event.registry.registerModule(DispenserModule, Item.getItemFromBlock(MCBlocks.DISPENSER), { boat, module -> SimpleModuleInventory(3*5, "dispenser", boat, module) })
        event.registry.registerModule(DivingModule, DivingBottleItem)
        event.registry.registerModule(RudderModule, RudderItem)
        event.registry.registerModule(DropperModule, Item.getItemFromBlock(MCBlocks.DROPPER), { boat, module -> SimpleModuleInventory(3*5, "dropper", boat, module) })
        event.registry.registerModule(BatteryModule, Item.getItemFromBlock(BlockBoatBattery))
        event.registry.registerModule(FluidTankModule, Item.getItemFromBlock(BlockBoatTank))
        event.registry.registerModule(ChunkLoadingModule, ChunkLoaderItem, restriction = MBConfig::chunkloaderAllowed)
        plugins.forEach { it.registerModules(event.registry) }
    }

    @SubscribeEvent
    fun registerBlocks(e: RegistryEvent.Register<Block>) {
        e.registry.registerAll(*Blocks.list.toTypedArray())
        GameRegistry.registerTileEntity(TileEntityEnergyUnloader::class.java, BlockEnergyUnloader.registryName)
        GameRegistry.registerTileEntity(TileEntityEnergyLoader::class.java, BlockEnergyLoader.registryName)
        GameRegistry.registerTileEntity(TileEntityFluidUnloader::class.java, BlockFluidUnloader.registryName)
        GameRegistry.registerTileEntity(TileEntityFluidLoader::class.java, BlockFluidLoader.registryName)
        GameRegistry.registerTileEntity(TileEntityMappingTable::class.java, BlockMappingTable.registryName)
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

    @SubscribeEvent
    fun registerRecipes(e: RegistryEvent.Register<IRecipe>) {
        e.registry.register(ModularBoatColoringRecipe)
        e.registry.register(GoldenTicketCopyRecipe)
        e.registry.register(UpgradeToGoldenTicketRecipe)
        e.registry.register(MapWithPathRecipe)
    }

    fun getLocalMapStorage(): MapStorage {
        val side = FMLCommonHandler.instance().side
        try {
            return when {
                side == Side.CLIENT && FMLCommonHandler.instance().minecraftServerInstance == null -> Minecraft.getMinecraft().world.mapStorage!!
                side == Side.CLIENT && FMLCommonHandler.instance().minecraftServerInstance != null || side == Side.SERVER
                -> FMLCommonHandler.instance().minecraftServerInstance.getWorld(0).mapStorage!!
                else -> throw RuntimeException("should not happen (side is null ?)")
            }
        } catch (e: Throwable) {
            MoarBoats.logger.error("Something broke in MoarBoats#getLocalMapStorage(), something in the code might be very wrong! Please report.", e)
            throw e
        }
    }
}