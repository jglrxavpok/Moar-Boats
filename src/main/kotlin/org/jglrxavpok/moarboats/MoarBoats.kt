package org.jglrxavpok.moarboats

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import net.minecraft.block.Block
import net.minecraft.init.Items as MCItems
import net.minecraft.init.Blocks as MCBlocks
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.item.ItemBlock
import net.minecraft.item.Item
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.common.modules.EngineTest
import org.jglrxavpok.moarboats.common.modules.inventories.ChestModuleInventory
import org.jglrxavpok.moarboats.common.modules.inventories.EngineModuleInventory
import org.jglrxavpok.moarboats.modules.BoatModuleRegistry


@Mod(modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", modid = MoarBoats.ModID, dependencies = "required-after:forgelin;",
        name = "Moar Boats", version = "1.0-indev")
object MoarBoats {
    const val ModID = "moarboats"

    lateinit var logger: Logger

    @SidedProxy(clientSide = "org.jglrxavpok.moarboats.client.Proxy", serverSide = "org.jglrxavpok.moarboats.server.Proxy")
    lateinit var proxy: MoarBoatsProxy

    val network = SimpleNetworkWrapper(ModID)

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        BoatModuleRegistry.registerModule(ResourceLocation("moarboats:furnace_engine"), Item.getItemFromBlock(MCBlocks.FURNACE), EngineTest, { boat, module -> EngineModuleInventory(boat, module) })
        BoatModuleRegistry.registerModule(ResourceLocation("moarboats:chest"), Item.getItemFromBlock(MCBlocks.CHEST), ChestModule, { boat, module -> ChestModuleInventory(boat, module) })
        MinecraftForge.EVENT_BUS.register(this)
        logger = event.modLog
        proxy.preInit()
        println("Hello from Kotlin!")
        logger.error("Hi from logger!")
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