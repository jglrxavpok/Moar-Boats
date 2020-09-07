package org.jglrxavpok.moarboats.integration

import net.minecraft.data.DataGenerator
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.StartupMessageManager
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent
import net.minecraftforge.registries.IForgeRegistry
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.network.MBMessageHandler
import org.jglrxavpok.moarboats.common.network.MoarBoatsPacket

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
/**
 * Annotation necessary to make MoarBoats recognize your plugin. 'dependency' is the modid of the mod the plugin depends on
 * (multiple plugins for a single mod is allowed)
 *
 * **Warning to Kotlin users**: Make sure your class is an actual class, not an object declaration!
 */
annotation class MoarBoatsIntegration(val dependency: String)

interface MoarBoatsPlugin {
    fun preInit() {}
    fun init() {}
    fun postInit() {}
    fun registerModules(registry: IForgeRegistry<BoatModuleEntry>) {}
    fun handlers(): List<MBMessageHandler<out MoarBoatsPacket, out MoarBoatsPacket?>> = emptyList()

    fun registerAsEventSubscriber() {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @OnlyIn(Dist.CLIENT)
    fun registerModuleRenderers(registry: IForgeRegistry<BoatModuleRenderer>) {}

    /**
     * MoarBoats passes the FMLClientSetupEvent to its plugins when it receives it.
     * It is not necessary to register screen factories for modules as MoarBoats will do it automatically
     */
    @OnlyIn(Dist.CLIENT)
    fun onClientSetup(event: FMLClientSetupEvent) {}

    fun registerProviders(event: GatherDataEvent, generator: DataGenerator, existingFileHelper: ExistingFileHelper) {}
    fun populateBoatTypes() {}
}

/**
 * This method looks for classes that implement MoarBoatsPlugin & have @MoarBoatsIntegration, check to see if their dependency
 * (the mod for which the plugin is made) is present and loads the plugin if that's the case
 */
fun LoadIntegrationPlugins(): List<MoarBoatsPlugin> {
    val startTime = System.nanoTime()
    MoarBoats.logger.info("Starting search for plugins")
    StartupMessageManager.addModMessage("[Moar Boats] Starting search for plugins")

    /**
     * Tries to load the plugin from the given ASMData, also verifies that the dependency is present
     */
    fun tryGetPlugin(className: String): MoarBoatsPlugin? {
        try {
            val clazz = MoarBoatsIntegration::class.java.classLoader.loadClass(className)
            val valid = with(clazz) {
                if(!MoarBoatsPlugin::class.java.isAssignableFrom(clazz))
                    throw IllegalStateException("Found MoarBoatsIntegration annotation on a class that does not implement MoarBoatsPlugin")
                val dependency = clazz.getAnnotation(MoarBoatsIntegration::class.java).dependency
                MoarBoats.logger.info("Found candidate $className with dependency $dependency")
                if(ModList.get().isLoaded(dependency))
                    true
                else {
                    MoarBoats.logger.warn("Dependency $dependency not found for plugin $canonicalName")
                    false
                }
            }
            if(valid) {
                return clazz.newInstance() as MoarBoatsPlugin
            }
        } catch (t: Throwable) {
            MoarBoats.logger.error("Failed to load plugin $className", t)
        }
        return null
    }
    // Go through all classes that have @MoarBoatsIntegration
    val classes = ModList.get().allScanData
            .flatMap { it.annotations }
            .filter { it.annotationType.className == MoarBoatsIntegration::class.java.canonicalName }
            .filter {
                try {
                    val clazz = Class.forName(it.classType.className)
                    clazz.isAnnotationPresent(MoarBoatsIntegration::class.java)
                } catch (e: Exception) {
                    // Google Classpath somehow found a class that doesn't exist or doesn't load correctly ¯\_(ツ)_/¯
                    false
                } catch (e: Error) {
                    // Google Classpath somehow found a class that doesn't exist or doesn't load correctly ¯\_(ツ)_/¯
                    false
                }
            }
    val plugins = mutableListOf<MoarBoatsPlugin>()
    classes.forEach { info ->
        MoarBoats.logger.debug("Looking for potential plugin in ${info.classType.className}")
        val plugin = tryGetPlugin(info.classType.className) ?: return@forEach
        plugin.let { plugins += it }
    }

    // Just for rendering, display "None" if no plugin loaded, "plugin1, plugin2, ..." if some loaded
    val pluginList = if(plugins.isEmpty()) "None" else plugins.joinToString(", ") { it::class.java.simpleName }
    val endTime = System.nanoTime()
    val time = (endTime-startTime)/1_000_000
    MoarBoats.logger.info("Found and loaded plugins: $pluginList. Took ${time}ms")
    StartupMessageManager.addModMessage("[Moar Boats] Found plugins: $pluginList")
    return plugins
}