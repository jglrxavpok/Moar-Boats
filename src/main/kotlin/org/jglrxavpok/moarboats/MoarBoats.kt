package org.jglrxavpok.moarboats

import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.block.Block
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.storage.WorldSavedDataStorage
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.registries.RegistryBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.registerModule
import org.jglrxavpok.moarboats.client.ClientEvents
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.blocks.*
import org.jglrxavpok.moarboats.common.items.*
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory
import org.jglrxavpok.moarboats.common.tileentity.*
import org.jglrxavpok.moarboats.integration.LoadIntegrationPlugins
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin
import java.net.URL
import java.util.function.Supplier
import net.minecraft.init.Blocks as MCBlocks
import net.minecraft.init.Items as MCItems

@KotlinEventBusSubscriber
@Mod(value = MoarBoats.ModID)
object MoarBoats {
    const val ModID = "moarboats"

    internal var dedicatedServerInstance: DedicatedServer? = null

    val logger: Logger = LogManager.getLogger()
    val NetworkingProtocolVersion = "v1.0"

    val network = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation(ModID, "network"))
            .networkProtocolVersion { NetworkingProtocolVersion }
            .clientAcceptedVersions { NetworkingProtocolVersion == it }
            .serverAcceptedVersions { NetworkingProtocolVersion == it }
            .simpleChannel()

    val PatreonList: List<String> by lazy {
        try {

            URL("https://gist.githubusercontent.com/jglrxavpok/07bcda98b7174f07b49fb0df1edda03a/raw/a07167925fab89205f1be16fbbfc628b8478de81/jglrxavpok_patreons_list_uuid")
                    .readText()
                    .lines() +
                    "0d2e2d40-72c3-4b2d-b221-ab94a791d5bc" + // jglrxavpok
                    "326e2676-d8bc-4b57-a859-5cb29cef0301" // FrenchUranoscopidae
        } catch (any: Throwable) {
            MoarBoats.logger.info("Could not retrieve Patreon list because of: ", any)
            emptyList<String>()
        }
    }

    val MachineMaterial = Material(MaterialColor.IRON, false, true, true, true, true, false, false, EnumPushReaction.BLOCK)

    val CreativeTab = object: ItemGroup("moarboats") {
        @OnlyIn(Dist.CLIENT)
        override fun createIcon(): ItemStack {
            return ItemStack(ModularBoatItem)
        }
    }

    lateinit var plugins: List<MoarBoatsPlugin>

    /* FIXME: For when Forge reimplements fluids
    lateinit var TileEntityFluidLoaderType: TileEntityType<TileEntityFluidLoader>
    lateinit var TileEntityFluidUnloaderType: TileEntityType<TileEntityFluidUnloader>
     */
    lateinit var TileEntityEnergyLoaderType: TileEntityType<TileEntityEnergyLoader>
    lateinit var TileEntityEnergyUnloaderType: TileEntityType<TileEntityEnergyUnloader>
    lateinit var TileEntityMappingTableType: TileEntityType<TileEntityMappingTable>

    init {
        FMLKotlinModLoadingContext.get().modEventBus.addListener(ClientEvents::doClientStuff)
        FMLKotlinModLoadingContext.get().modEventBus.addListener(this::initDedicatedServer)
    }

    @SubscribeEvent
    fun preInit(event: FMLCommonSetupEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(ItemEventHandler)
        MinecraftForge.EVENT_BUS.register(MoarBoatsConfig::javaClass)
        proxy.preInit()
/* FIXME        ForgeChunkManager.setForcedChunkLoadingCallback(MoarBoats) { tickets, world ->
            for(ticket in tickets) {
                for(pos in ticket.chunkList) {
                    ForgeChunkManager.forceChunk(ticket, pos)
                }
            }
        }*/

        plugins = LoadIntegrationPlugins(event)
        plugins.forEach(MoarBoatsPlugin::preInit)
        DataSerializers.registerSerializer(ResourceLocationsSerializer)
        DataSerializers.registerSerializer(UniqueIDSerializer)
        plugins.forEach(MoarBoatsPlugin::init)
        plugins.forEach(MoarBoatsPlugin::postInit)
    }

    @SubscribeEvent
    fun init(event: FMLInitializationEvent) {
        proxy.init() // FIXME: Packet initialization
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
    fun initDedicatedServer(event: FMLDedicatedServerSetupEvent) {
        dedicatedServerInstance = event.serverSupplier.get()
    }

    @SubscribeEvent
    fun registerModules(event: RegistryEvent.Register<BoatModuleEntry>) {
        event.registry.registerModule(ResourceLocation("moarboats:furnace_engine"), Item.getItemFromBlock(MCBlocks.FURNACE), FurnaceEngineModule, { boat, module -> EngineModuleInventory(boat, module) })
        event.registry.registerModule(ResourceLocation("moarboats:chest"), Item.getItemFromBlock(MCBlocks.CHEST), ChestModule, { boat, module -> ChestModuleInventory(boat, module) })
        event.registry.registerModule(ResourceLocation("moarboats:helm"), HelmItem, HelmModule, { boat, module -> SimpleModuleInventory(1, "helm", boat, module) })
        event.registry.registerModule(ResourceLocation("moarboats:fishing"), MCItems.FISHING_ROD, FishingModule, { boat, module -> SimpleModuleInventory(1, "fishing", boat, module) })
        event.registry.registerModule(SeatModule, SeatItem)
        event.registry.registerModule(AnchorModule, MCBlocks.ANVIL.asItem())
        event.registry.registerModule(SolarEngineModule, MCBlocks.DAYLIGHT_DETECTOR.asItem())
        event.registry.registerModule(CreativeEngineModule, CreativeEngineItem)
        event.registry.registerModule(IceBreakerModule, IceBreakerItem)
        event.registry.registerModule(SonarModule, MCBlocks.NOTE_BLOCK.asItem())
        event.registry.registerModule(DispenserModule, MCBlocks.DISPENSER.asItem(), { boat, module -> SimpleModuleInventory(3*5, "dispenser", boat, module) })
        event.registry.registerModule(DivingModule, DivingBottleItem)
        event.registry.registerModule(RudderModule, RudderItem)
        event.registry.registerModule(DropperModule, MCBlocks.DROPPER.asItem(), { boat, module -> SimpleModuleInventory(3*5, "dropper", boat, module) })
        event.registry.registerModule(BatteryModule, BlockBoatBattery.asItem())
        event.registry.registerModule(FluidTankModule, BlockBoatTank.asItem())
        event.registry.registerModule(ChunkLoadingModule, ChunkLoaderItem, restriction = { MoarBoatsConfig.chunkLoader.allowed })
        event.registry.registerModule(OarEngineModule, OarsItem)
        plugins.forEach { it.registerModules(event.registry) }
    }

    @SubscribeEvent
    fun registerBlocks(e: RegistryEvent.Register<Block>) {
        e.registry.registerAll(*Blocks.list.toTypedArray())
    }

    @SubscribeEvent
    fun registerTileEntities(evt: RegistryEvent.Register<TileEntityType<*>>) {
        TileEntityEnergyUnloaderType = evt.registerTE(::TileEntityEnergyUnloader, BlockEnergyUnloader.registryName)
        TileEntityEnergyUnloaderType = evt.registerTE(::TileEntityEnergyUnloader, BlockEnergyUnloader.registryName)
        TileEntityEnergyLoaderType = evt.registerTE(::TileEntityEnergyLoader, BlockEnergyLoader.registryName)
        /* FIXME
        TileEntityFluidUnloaderType = evt.registerTE(::TileEntityFluidUnloader, BlockFluidUnloader.registryName)
        TileEntityFluidLoaderType = evt.registerTE(::TileEntityFluidLoader, BlockFluidLoader.registryName)
         */
        TileEntityMappingTableType = evt.registerTE(::TileEntityMappingTable, BlockMappingTable.registryName)
    }

    private inline fun <reified TE: TileEntity> RegistryEvent.Register<TileEntityType<*>>.registerTE(noinline constructor: () -> TE, registryName: ResourceLocation?): TileEntityType<TE> {
        return TileEntityType.register(registryName.toString(), TileEntityType.Builder.create<TE>(constructor))
    }

    @SubscribeEvent
    fun registerItems(e: RegistryEvent.Register<Item>) {
        e.registry.registerAll(*Items.list.toTypedArray())
        for (block in Blocks.list) {
            e.registry.register(ItemBlock(block, Item.Properties().group(MoarBoats.CreativeTab)).setRegistryName(block.registryName))
        }
    }

    @SubscribeEvent
    fun registerEntities(e: RegistryEvent.Register<EntityType<*>>) {
        e.registry.registerAll(*EntityEntries.list.toTypedArray())
    }

    @SubscribeEvent
    fun registerRecipes(e: RegistryEvent.Register<IRecipe>) {
        e.registry.register(ModularBoatColoringRecipe)
        e.registry.register(GoldenTicketCopyRecipe)
        e.registry.register(UpgradeToGoldenTicketRecipe)
        e.registry.register(MapWithPathRecipe)
    }

    fun getLocalMapStorage(dimensionType: DimensionType = DimensionType.OVERWORLD): WorldSavedDataStorage {
        try {
            return DistExecutor.runForDist(
                    // client
                    { Supplier {  ->
                        when {
                            Minecraft.getInstance().integratedServer == null /* Client only */ -> Minecraft.getInstance().world.savedDataStorage!!
                            Minecraft.getInstance().integratedServer != null /* LAN */ -> Minecraft.getInstance().integratedServer!!.getWorld(dimensionType).savedDataStorage!!

                            else -> throw IllegalStateException("The server instance is neither non-null nor null. Something deeply broke somewhere")
                        }
                    }},

                    // server
                    { Supplier<WorldSavedDataStorage> { ->
                        dedicatedServerInstance!!.getWorld(dimensionType).savedDataStorage!!
                    }}
            )
        } catch (e: Throwable) {
            MoarBoats.logger.error("Something broke in MoarBoats#getLocalMapStorage(), something in the code might be very wrong! Please report.", e)
            throw e
        }
    }
}