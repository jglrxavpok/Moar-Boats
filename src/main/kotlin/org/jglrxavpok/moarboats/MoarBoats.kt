package org.jglrxavpok.moarboats

import net.minecraft.client.Minecraft
import net.minecraft.core.NonNullList
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.packs.PackType
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.AirItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.DimensionDataStorage
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry
import net.minecraftforge.registries.NewRegistryEvent
import net.minecraftforge.registries.RegistryBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.registerModule
import org.jglrxavpok.moarboats.client.ClientEvents
import org.jglrxavpok.moarboats.client.DummyDimensionSavedDataManager
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.blocks.*
import org.jglrxavpok.moarboats.common.containers.*
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.*
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory
import org.jglrxavpok.moarboats.common.tileentity.*
import org.jglrxavpok.moarboats.datagen.JsonModelGenerator
import org.jglrxavpok.moarboats.datagen.UtilityBoatRecipes
import org.jglrxavpok.moarboats.integration.LoadIntegrationPlugins
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin
import org.jglrxavpok.moarboats.server.ServerEvents
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import java.net.URL
import java.util.*
import java.util.concurrent.Callable
import java.util.function.Supplier

import net.minecraft.world.level.block.Blocks as MCBlocks

@Mod(MoarBoats.ModID)
object MoarBoats {

    const val ModID = "moarboats"

    internal var dedicatedServerInstance: MinecraftServer? = null

    val logger: Logger = LogManager.getLogger()
    val NetworkingProtocolVersion = "v1.1"

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

    val MachineMaterial = Material(MaterialColor.METAL, false, true, true, true, true, false, PushReaction.BLOCK)

    val MainCreativeTab = object: CreativeModeTab("moarboats") {

        @OnlyIn(Dist.CLIENT)
        override fun makeIcon(): ItemStack {
            return ItemStack(MBItems.ModularBoats[DyeColor.WHITE]!!.get())
        }

        override fun fillItemList(items: NonNullList<ItemStack>) {
            for (blockEntry in MBBlocks.Registry.entries) {
                val block = blockEntry.get()
                if(block.asItem() !is AirItem) {
                    block.fillItemCategory(this, items)
                }
            }
            for (itemEntry in MBItems.Registry.entries) {
                val item = itemEntry.get()
                item.fillItemCategory(this, items)
            }
        }
    }

    val UtilityBoatTab = object: CreativeModeTab("moarboats_utility") {

        @OnlyIn(Dist.CLIENT)
        override fun makeIcon(): ItemStack {
            return ItemStack(MBItems.CraftingTableBoats[BoatType.OAK]!!.get())
        }

        override fun fillItemList(items: NonNullList<ItemStack>) {
            for (itemEntry in MBItems.Registry.entries) {
                val item = itemEntry.get()
                item.fillItemCategory(this, items)
            }
        }
    }

    lateinit var plugins: List<MoarBoatsPlugin>

    lateinit var TileEntityFluidLoaderType: BlockEntityType<TileEntityFluidLoader>
    lateinit var TileEntityFluidUnloaderType: BlockEntityType<TileEntityFluidUnloader>
    lateinit var TileEntityEnergyLoaderType: BlockEntityType<TileEntityEnergyLoader>
    lateinit var TileEntityEnergyUnloaderType: BlockEntityType<TileEntityEnergyUnloader>
    lateinit var TileEntityMappingTableType: BlockEntityType<TileEntityMappingTable>

    init {
        MOD_CONTEXT.getKEventBus().addListener(ClientEvents::doClientStuff)
        MOD_CONTEXT.getKEventBus().addListener(this::setup)
        MOD_CONTEXT.getKEventBus().addListener(this::initDedicatedServer)
        MOD_CONTEXT.getKEventBus().addListener(this::postLoad)
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MoarBoatsConfig.spec)

        MinecraftForge.EVENT_BUS.register(MoarBoatsGuiHandler)

        MinecraftForge.EVENT_BUS.register(ServerEvents)

        BoatType.populateBoatTypeCache() // we are doing this so early that it is possible we cannot handle boats from other mods

        MBBlocks.Registry.register(MOD_CONTEXT.getKEventBus())
        MBItems.Registry.register(MOD_CONTEXT.getKEventBus())
        EntityEntries.Registry.register(MOD_CONTEXT.getKEventBus())
        Modules.Registry.register(MOD_CONTEXT.getKEventBus())

        DistExecutor.runForDist({
            Supplier {

            }
        }, {
            Supplier {
            }
        })
        plugins = LoadIntegrationPlugins()
    }

    fun getLocalMapStorage(dimensionType: ResourceKey<Level> = Level.OVERWORLD): DimensionDataStorage {
        try {
            return DistExecutor.safeRunForDist(
                    // client
                    {
                        DistExecutor.SafeSupplier { ->
                            when {
                                Minecraft.getInstance().singleplayerServer != null /* LAN */ -> Minecraft.getInstance().singleplayerServer!!.getLevel(dimensionType)?.dataStorage
                                        ?: error("Tried to get save data of an nonexistent dimension type? $dimensionType")
                                else -> DummyDimensionSavedDataManager
                            }
                        }
                    },

                    // server
                    {
                        DistExecutor.SafeSupplier<DimensionDataStorage> { ->
                            dedicatedServerInstance!!.getLevel(dimensionType)?.dataStorage ?: error("Tried to get save data of an nonexistent dimension type? $dimensionType")
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
        EntityDataSerializers.registerSerializer(ResourceLocationsSerializer)
        EntityDataSerializers.registerSerializer(UniqueIDSerializer)
        plugins.forEach(MoarBoatsPlugin::init)
    }

    fun postLoad(event: FMLLoadCompleteEvent) {
        MoarBoatsPacketList.registerAll()
        plugins.forEach(MoarBoatsPlugin::postInit)

        DistExecutor.callWhenOn(Dist.CLIENT) {
            Callable<Unit> {ClientEvents.postInit(event)}
        }

        BoatType.populateBoatTypeCache()
    }

    fun initDedicatedServer(event: ServerStartingEvent) {
        dedicatedServerInstance = event.server
    }

    @Mod.EventBusSubscriber(modid = ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
    object RegistryEvents {
        @SubscribeEvent
        fun createRegistry(e: NewRegistryEvent) {
            plugins.forEach { it.populateBoatTypes() }
        }

        @SubscribeEvent
        fun registerTileEntities(evt: RegistryEvent.Register<TileEntityType<*>>) {
            TileEntityEnergyUnloaderType = evt.registerTE(evt, ::TileEntityEnergyUnloader, BlockEnergyUnloader)
            TileEntityEnergyLoaderType = evt.registerTE(evt, ::TileEntityEnergyLoader, BlockEnergyLoader)
            TileEntityFluidUnloaderType = evt.registerTE(evt, ::TileEntityFluidUnloader, BlockFluidUnloader)
            TileEntityFluidLoaderType = evt.registerTE(evt, ::TileEntityFluidLoader, BlockFluidLoader)
            TileEntityMappingTableType = evt.registerTE(evt, ::TileEntityMappingTable, BlockMappingTable)
        }

        private inline fun <reified TE: BlockEntity> RegistryEvent.Register<TileEntityType<*>>.registerTE(evt: RegistryEvent.Register<TileEntityType<*>>, noinline constructor: () -> TE, block: Block): TileEntityType<TE> {
            val type = TileEntityType.Builder.of(Supplier<TE> { constructor() }, block).build(null).setRegistryName(block.registryName)
            evt.registry.register(type)
            return type as TileEntityType<TE>
        }

        @SubscribeEvent
        fun registerRecipes(event: RegistryEvent.Register<RecipeSerializer<*>>) {
            MBRecipeSerializers.MapWithPath = MBRecipeSerializers.SingletonSerializer(MapWithPathRecipe).apply { setRegistryName(ResourceLocation(ModID, "map_with_path")) }.also(event.registry::register)
            MBRecipeSerializers.BoatColoring = MBRecipeSerializers.SingletonSerializer(ModularBoatColoringRecipe).apply { setRegistryName(ResourceLocation(ModID, "color_modular_boat")) }.also(event.registry::register)
            MBRecipeSerializers.UpgradeToGoldenTicket = MBRecipeSerializers.SingletonSerializer(UpgradeToGoldenTicketRecipe).apply { setRegistryName(ResourceLocation(ModID, "upgrade_to_golden_ticket")) }.also(event.registry::register)
            MBRecipeSerializers.CopyGoldenTicket = MBRecipeSerializers.SingletonSerializer(GoldenTicketCopyRecipe).apply { setRegistryName(ResourceLocation(ModID, "copy_golden_ticket")) }.also(event.registry::register)
            MBRecipeSerializers.ShulkerBoat = SpecialRecipeSerializer { _ -> ShulkerBoatRecipe }.apply { setRegistryName(ModID, "shulker_boat") }.also(event.registry::register)

            event.registry.register(MBRecipeSerializers.MapWithPath)
            event.registry.register(MBRecipeSerializers.BoatColoring)
            event.registry.register(MBRecipeSerializers.UpgradeToGoldenTicket)
            event.registry.register(MBRecipeSerializers.CopyGoldenTicket)
            event.registry.register(MBRecipeSerializers.ShulkerBoat)
        }

        @SubscribeEvent
        fun gatherData(event: GatherDataEvent) {
            val generator = event.generator
            val delegate = event.existingFileHelper
            val existingFileHelper = object: ExistingFileHelper(emptyList(), Collections.emptySet(), true, null, null) {
                override fun exists(loc: ResourceLocation?, type: PackType?, pathSuffix: String?, pathPrefix: String?): Boolean {
                    if(loc?.namespace == ModID)
                        return true
                    return delegate.exists(loc, type, pathSuffix, pathPrefix)
                }
            }
            generator.addProvider(event.includeClient(), JsonModelGenerator(generator, ModID, "minecraft", existingFileHelper))
            generator.addProvider(event.includeServer(), UtilityBoatRecipes(generator))
            plugins.forEach { it.registerProviders(event, generator, existingFileHelper) }
            generator.run()
        }
    }
}
