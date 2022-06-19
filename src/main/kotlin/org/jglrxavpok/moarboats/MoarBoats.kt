package org.jglrxavpok.moarboats

import net.minecraft.core.NonNullList
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.packs.PackType
import net.minecraft.world.item.AirItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.DimensionDataStorage
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.DistExecutor.SafeSupplier
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.registries.NewRegistryEvent
import net.minecraftforge.registries.RegistryBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.client.ClientEvents
import org.jglrxavpok.moarboats.client.ClientProxy
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.items.MBRecipeSerializers
import org.jglrxavpok.moarboats.datagen.JsonModelGenerator
import org.jglrxavpok.moarboats.datagen.UtilityBoatRecipes
import org.jglrxavpok.moarboats.server.ServerEvents
import org.jglrxavpok.moarboats.server.ServerProxy
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import java.net.URL
import java.util.*
import java.util.concurrent.Callable

@Mod(MoarBoats.ModID)
object MoarBoats {

    const val ModID = "moarboats"

    internal var dedicatedServerInstance: MinecraftServer? = null

    val logger: Logger = LogManager.getLogger()
    val NetworkingProtocolVersion = "v1.1"

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

    val proxy: Proxy

    init {
        proxy = DistExecutor.safeRunForDist(
            // client
            {
                SafeSupplier(::ClientProxy)
            },

            // server
            {
                SafeSupplier(::ServerProxy)
            }
        )

        MOD_CONTEXT.getKEventBus().addListener(ClientEvents::doClientStuff)
        MOD_CONTEXT.getKEventBus().addListener(this::setup)
        MOD_CONTEXT.getKEventBus().addListener(this::postLoad)
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MoarBoatsConfig.spec)

        MinecraftForge.EVENT_BUS.register(MoarBoatsGuiHandler)

        MinecraftForge.EVENT_BUS.register(ServerEvents)

        BoatType.populateBoatTypeCache() // we are doing this so early that it is possible we cannot handle boats from other mods

        val bus = MOD_CONTEXT.getKEventBus()
        MBBlocks.Registry.register(bus)
        MBItems.Registry.register(bus)
        EntityEntries.Registry.register(bus)
        BlockEntities.Registry.register(bus)
        ContainerTypes.Registry.register(bus)
        MBRecipeSerializers.Registry.register(bus)
    }

    fun getLocalMapStorage(dimensionType: ResourceKey<Level> = Level.OVERWORLD): DimensionDataStorage {
        try {
            return proxy.get(dimensionType)
        } catch(e: Throwable) {
            MoarBoats.logger.error("Something broke in MoarBoats#getLocalMapStorage(), something in the code might be very wrong! Please report.", e)
            throw e
        }
    }

    fun setup(event: FMLCommonSetupEvent) {
        EntityDataSerializers.registerSerializer(ResourceLocationsSerializer)
        EntityDataSerializers.registerSerializer(UniqueIDSerializer)
    }

    fun postLoad(event: FMLLoadCompleteEvent) {
        MoarBoatsPacketList.registerAll()

        DistExecutor.callWhenOn(Dist.CLIENT) {
            Callable<Unit> {ClientEvents.postInit(event)}
        }

        BoatType.populateBoatTypeCache()
    }

    @Mod.EventBusSubscriber(modid = ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
    object RegistryEvents {

        @SubscribeEvent
        fun onNewRegistry(event: NewRegistryEvent) {
            BoatModuleRegistry.Registry = event.create(RegistryBuilder<BoatModuleEntry>()
                .allowModification()
                .setName(Modules.RegistryKey.location())
            )
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
            generator.run()
        }
    }
}
