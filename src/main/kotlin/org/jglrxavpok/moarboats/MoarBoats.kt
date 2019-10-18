package org.jglrxavpok.moarboats

import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.block.material.PushReaction
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.entity.EntityType
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.*
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.storage.DimensionSavedDataManager
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
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
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.*
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory
import org.jglrxavpok.moarboats.common.tileentity.*
import org.jglrxavpok.moarboats.integration.LoadIntegrationPlugins
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin
import java.net.URL
import java.util.concurrent.Callable
import java.util.function.Supplier
import net.minecraft.block.Blocks as MCBlocks
import net.minecraft.item.Items as MCItems

@Mod(MoarBoats.ModID)
object MoarBoats {

    const val ModID = "moarboats"

    internal var dedicatedServerInstance: DedicatedServer? = null

    val logger: Logger = LogManager.getLogger()
    val NetworkingProtocolVersion = "v1.0"

    val registryID = ResourceLocation(ModID, "modules")
    val network = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation(ModID, "network"))
            .networkProtocolVersion {NetworkingProtocolVersion}
            .clientAcceptedVersions {NetworkingProtocolVersion == it}
            .serverAcceptedVersions {NetworkingProtocolVersion == it}
            .simpleChannel()

    val PatreonList: List<String> by lazy {
        try {

            URL("https://gist.githubusercontent.com/jglrxavpok/07bcda98b7174f07b49fb0df1edda03a/raw/a07167925fab89205f1be16fbbfc628b8478de81/jglrxavpok_patreons_list_uuid")
                    .readText()
                    .lines() +
                    "0d2e2d40-72c3-4b2d-b221-ab94a791d5bc" + // jglrxavpok
                    "326e2676-d8bc-4b57-a859-5cb29cef0301" // FrenchUranoscopidae
        } catch(any: Throwable) {
            MoarBoats.logger.info("Could not retrieve Patreon list because of: ", any)
            emptyList<String>()
        }
    }

    val MachineMaterial = Material(MaterialColor.IRON, false, true, true, true, true, false, false, PushReaction.BLOCK)

    val CreativeTab = object: ItemGroup("moarboats") {

        @OnlyIn(Dist.CLIENT)
        override fun createIcon(): ItemStack {
            return ItemStack(ModularBoatItem[DyeColor.WHITE])
        }
    }

    lateinit var plugins: List<MoarBoatsPlugin>

    lateinit var TileEntityFluidLoaderType: TileEntityType<TileEntityFluidLoader>
    lateinit var TileEntityFluidUnloaderType: TileEntityType<TileEntityFluidUnloader>
    lateinit var TileEntityEnergyLoaderType: TileEntityType<TileEntityEnergyLoader>
    lateinit var TileEntityEnergyUnloaderType: TileEntityType<TileEntityEnergyUnloader>
    lateinit var TileEntityMappingTableType: TileEntityType<TileEntityMappingTable>

    init {
        FMLKotlinModLoadingContext.get().modEventBus.addListener {event: FMLClientSetupEvent -> ClientEvents.doClientStuff(event)}
        FMLKotlinModLoadingContext.get().modEventBus.addListener {event: FMLCommonSetupEvent -> setup(event)}
        FMLKotlinModLoadingContext.get().modEventBus.addListener {event: FMLDedicatedServerSetupEvent -> initDedicatedServer(event)}
        FMLKotlinModLoadingContext.get().modEventBus.addListener {event: FMLLoadCompleteEvent -> postLoad(event)}
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MoarBoatsConfig.spec)

        MinecraftForge.EVENT_BUS.register(MoarBoatsGuiHandler)
        plugins = LoadIntegrationPlugins()
    }

    fun getLocalMapStorage(dimensionType: DimensionType = DimensionType.OVERWORLD): DimensionSavedDataManager {
        try {
            return DistExecutor.runForDist(
                    // client
                    {
                        Supplier { ->
                            when {
                                Minecraft.getInstance().integratedServer != null /* LAN */ -> Minecraft.getInstance().integratedServer!!.getWorld(dimensionType).savedData

                                else -> throw IllegalStateException("The server instance is neither non-null nor null. Something deeply broke somewhere")
                            }
                        }
                    },

                    // server
                    {
                        Supplier<DimensionSavedDataManager> { ->
                            dedicatedServerInstance!!.getWorld(dimensionType).savedData
                        }
                    }
            )
        } catch(e: Throwable) {
            MoarBoats.logger.error("Something broke in MoarBoats#getLocalMapStorage(), something in the code might be very wrong! Please report.", e)
            throw e
        }
    }

    fun setup(event: FMLCommonSetupEvent) {
        plugins.forEach(MoarBoatsPlugin::preInit)
        DataSerializers.registerSerializer(ResourceLocationsSerializer)
        DataSerializers.registerSerializer(UniqueIDSerializer)
        plugins.forEach(MoarBoatsPlugin::init)
        /*        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY) {
                    Function<FMLPlayMessages.OpenContainer, Screen?> { container: FMLPlayMessages.OpenContainer -> MoarBoatsGuiHandler.dispatchGui(container) }
                }*/
    }

    fun postLoad(event: FMLLoadCompleteEvent) {
        MoarBoatsPacketList.registerAll()
        plugins.forEach(MoarBoatsPlugin::postInit)

        DistExecutor.callWhenOn(Dist.CLIENT) {
            Callable<Unit> {ClientEvents.postInit(event)}
        }
    }

    fun initDedicatedServer(event: FMLDedicatedServerSetupEvent) {
        dedicatedServerInstance = event.serverSupplier.get()
        /* TODO
        dedicatedServerInstance?.let {
            it.recipeManager.addRecipe(ModularBoatColoringRecipe)
            it.recipeManager.addRecipe(GoldenTicketCopyRecipe)
            it.recipeManager.addRecipe(UpgradeToGoldenTicketRecipe)
            it.recipeManager.addRecipe(MapWithPathRecipe)
        }*/
    }

    @KotlinEventBusSubscriber(modid = ModID, bus = KotlinEventBusSubscriber.Bus.MOD)
    object RegistryEvents {
        @SubscribeEvent
        fun createRegistry(e: RegistryEvent.NewRegistry) {
            BoatModuleRegistry.forgeRegistry = RegistryBuilder<BoatModuleEntry>()
                    .allowModification()
                    .setName(registryID)
                    .setType(BoatModuleEntry::class.java)
                    .create()
        }

        @SubscribeEvent
        fun registerModules(event: RegistryEvent.Register<BoatModuleEntry>) {
            event.registry.registerModule(ResourceLocation("moarboats:furnace_engine"), MCBlocks.FURNACE.asItem(), FurnaceEngineModule, {boat, module -> EngineModuleInventory(boat, module)})
            event.registry.registerModule(ResourceLocation("moarboats:chest"), MCBlocks.CHEST.asItem(), ChestModule, {boat, module -> ChestModuleInventory(boat, module)})
            event.registry.registerModule(ResourceLocation("moarboats:helm"), HelmItem, HelmModule, {boat, module -> SimpleModuleInventory(1, "helm", boat, module)})
            event.registry.registerModule(ResourceLocation("moarboats:fishing"), MCItems.FISHING_ROD, FishingModule, {boat, module -> SimpleModuleInventory(1, "fishing", boat, module)})
            event.registry.registerModule(SeatModule, SeatItem)
            event.registry.registerModule(AnchorModule, MCBlocks.ANVIL.asItem())
            event.registry.registerModule(SolarEngineModule, MCBlocks.DAYLIGHT_DETECTOR.asItem())
            event.registry.registerModule(CreativeEngineModule, CreativeEngineItem)
            event.registry.registerModule(IceBreakerModule, IceBreakerItem)
            event.registry.registerModule(SonarModule, MCBlocks.NOTE_BLOCK.asItem())
            event.registry.registerModule(DispenserModule, MCBlocks.DISPENSER.asItem(), {boat, module -> SimpleModuleInventory(3 * 5, "dispenser", boat, module)})
            event.registry.registerModule(DivingModule, DivingBottleItem)
            event.registry.registerModule(RudderModule, RudderItem)
            event.registry.registerModule(DropperModule, MCBlocks.DROPPER.asItem(), {boat, module -> SimpleModuleInventory(3 * 5, "dropper", boat, module)})
            event.registry.registerModule(BatteryModule, BlockBoatBattery.asItem())
            event.registry.registerModule(FluidTankModule, BlockBoatTank.asItem())
            event.registry.registerModule(ChunkLoadingModule, ChunkLoaderItem, restriction = MoarBoatsConfig.chunkLoader.allowed::get)
            event.registry.registerModule(OarEngineModule, OarsItem)
            plugins.forEach {it.registerModules(event.registry)}
        }

        @SubscribeEvent
        fun registerBlocks(e: RegistryEvent.Register<Block>) {
            e.registry.registerAll(*Blocks.list.toTypedArray())
        }

        @SubscribeEvent
        fun registerItems(e: RegistryEvent.Register<Item>) {
            e.registry.registerAll(*Items.list.toTypedArray())
            for(block in Blocks.list) {
                if(!e.registry.containsKey(block.registryName)) { // don't overwrite already existing items
                    e.registry.register(BlockItem(block, Item.Properties().group(MoarBoats.CreativeTab)).setRegistryName(block.registryName))
                }
            }
        }

        @SubscribeEvent
        fun registerTileEntities(evt: RegistryEvent.Register<TileEntityType<*>>) {
            TileEntityEnergyUnloaderType = evt.registerTE(evt, ::TileEntityEnergyUnloader, BlockEnergyUnloader)
            TileEntityEnergyLoaderType = evt.registerTE(evt, ::TileEntityEnergyLoader, BlockEnergyLoader)
            TileEntityFluidUnloaderType = evt.registerTE(evt, ::TileEntityFluidUnloader, BlockFluidUnloader)
            TileEntityFluidLoaderType = evt.registerTE(evt, ::TileEntityFluidLoader, BlockFluidLoader)
            TileEntityMappingTableType = evt.registerTE(evt, ::TileEntityMappingTable, BlockMappingTable)
        }

        private inline fun <reified TE: TileEntity> RegistryEvent.Register<TileEntityType<*>>.registerTE(evt: RegistryEvent.Register<TileEntityType<*>>, noinline constructor: () -> TE, block: Block): TileEntityType<TE> {
            val type = TileEntityType.Builder.create(Supplier<TE> { constructor() }, block).build(null).setRegistryName(block.registryName)
            evt.registry.register(type)
            return type as TileEntityType<TE>
        }

        @SubscribeEvent
        fun registerEntities(e: RegistryEvent.Register<EntityType<*>>) {
            e.registry.registerAll(*EntityEntries.list.toTypedArray())
        }

        @SubscribeEvent
        public fun onContainerRegistry(event: RegistryEvent.Register<ContainerType<*>>) {
            for(moduleEntry in BoatModuleRegistry.forgeRegistry.values) {
                event.registry.register(IForgeContainerType.create { windowId, inv, data ->
                    val player = inv.player
                    val boatID = data.readInt()
                    val boat = player.world.getEntityByID(boatID) as ModularBoatEntity
                    boat?.let {
                        return@create moduleEntry.module.createContainer(windowId, player as ClientPlayerEntity, it)
                    } ?: return@create null
                }.setRegistryName(moduleEntry.module.id));
            }
            event.registry.register(IForgeContainerType.create { windowId, inv, data ->
                val player = inv.player
                val boatID = data.readInt()
                val boat = player.world.getEntityByID(boatID) as ModularBoatEntity
                boat?.let {
                    return@create EmptyModuleContainer(windowId, inv, ChestModule, boat)
                }
            }.setRegistryName(ModID, "empty"))
            event.registry.register(IForgeContainerType.create { windowId, inv, data ->
                val player = inv.player
                val pos = data.readBlockPos()
                val te = player.world.getTileEntity(pos)
                te?.let {
                    return@create ContainerMappingTable(windowId, it as TileEntityMappingTable, inv)
                }
            }.setRegistryName(ModID, "mapping_table"))

            event.registry.register(IForgeContainerType.create { windowId, inv, data ->
                return@create EmptyContainer(windowId, inv)
            }.setRegistryName(ModID, "none"))

            DistExecutor.runWhenOn(Dist.CLIENT) {
                Runnable {
                    ClientEvents.initScreens()
                }
            }
        }

    }
}
