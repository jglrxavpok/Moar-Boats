package org.jglrxavpok.moarboats.integration

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.jglrxavpok.moarboats.MoarBoats

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

    fun registerAsEventSubscriber() {
        MinecraftForge.EVENT_BUS.register(this)
    }
}

/**
 * This method looks for classes that implement MoarBoatsPlugin & have @MoarBoatsIntegration, check to see if their dependency
 * (the mod for which the plugin is made) is present and loads the plugin if that's the case
 */
fun LoadIntegrationPlugins(event: FMLPreInitializationEvent): List<MoarBoatsPlugin> {

    /**
     * Tries to load the plugin from the given ASMData, also verifies that the dependency is present
     */
    fun tryGetPlugin(info: ASMDataTable.ASMData): MoarBoatsPlugin? {
        try {
            val clazz = Class.forName(info.className, true, MoarBoatsIntegration::class.java.classLoader)
            val valid = with(clazz) {
                if(!MoarBoatsPlugin::class.java.isAssignableFrom(this))
                    return@with false
                val integration = this.getAnnotation(MoarBoatsIntegration::class.java)
                MoarBoats.logger.info("Found candidate ${info.className} with dependency ${integration.dependency}")
                if(Loader.isModLoaded(integration.dependency))
                    true
                else {
                    MoarBoats.logger.warn("Dependency ${integration.dependency} not found for plugin $canonicalName")
                    false
                }
            }
            if(valid) {
                return clazz.newInstance() as MoarBoatsPlugin
            }
        } catch (t: Throwable) {
            MoarBoats.logger.error("Failed to load plugin ${info.className}", t)
        }
        return null
    }

    // Go through all classes that have @MoarBoatsIntegration
    val classes = event.asmData.getAll(MoarBoatsIntegration::class.java.canonicalName)//ClassPath.from(MoarBoats::class.java.classLoader).allClasses
    val plugins = mutableListOf<MoarBoatsPlugin>()
    MoarBoats.logger.info("Starting search for plugins")
    classes.forEach { info ->
        MoarBoats.logger.debug("Looking for potential plugin in ${info.className}")
        val plugin = tryGetPlugin(info)
        plugin?.let { plugins += it }
    }

    // Just for rendering, display "None" if no plugin loaded, "plugin1, plugin2, ..." if some loaded
    val pluginList = if(plugins.isEmpty()) "None" else plugins.joinToString(", ") { it::class.java.canonicalName }
    MoarBoats.logger.info("Found and loaded plugins: $pluginList")
    return plugins
}