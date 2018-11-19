package org.jglrxavpok.moarboats.integration.opencomputers

import com.google.common.cache.Cache
import li.cil.oc.api.Machine
import li.cil.oc.api.network.ManagedEnvironment
import li.cil.oc.api.network.Node
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.network.INetHandler
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLEventChannel
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.api.registerModule
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.network.MBMessageHandler
import org.jglrxavpok.moarboats.integration.MoarBoatsIntegration
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin
import org.jglrxavpok.moarboats.integration.opencomputers.architecture.BoatArchitecture
import org.jglrxavpok.moarboats.integration.opencomputers.client.OCRenderer
import org.jglrxavpok.moarboats.integration.opencomputers.network.SSyncMachineData
import java.lang.reflect.Modifier

@MoarBoatsIntegration("opencomputers|core")
class OpenComputerPlugin: MoarBoatsPlugin {

    companion object {
        @CapabilityInject(MachineHostCapability::class)
        @JvmStatic
        lateinit var HostCapability: Capability<MachineHostCapability>

        @JvmStatic
        val HostKey = ResourceLocation(MoarBoats.ModID, "opencomputer_host")

        fun getHost(boat: IControllable): BoatMachineHost? = boat.correspondingEntity.getCapability(HostCapability, null)?.host
        fun getNode(boat: IControllable): Node? = boat.correspondingEntity.getCapability(HostCapability, null)?.node
    }

    override fun handlers() = listOf(SSyncMachineData.Handler)

    override fun preInit() {
        super.preInit()
        registerAsEventSubscriber()
        CapabilityManager.INSTANCE.register(MachineHostCapability::class.java, MachineHostCapability.Storage) {throw UnsupportedOperationException()};
    }

    override fun postInit() {
        super.postInit()
        val ocClass = Class.forName("li.cil.oc.OpenComputers")
        val channel: FMLEventChannel = ocClass.getMethod("channel").invoke(null /* static method */) as FMLEventChannel
        println("HOOKED INTO OC CHANNEL")
        channel.register(this)
    }

    override fun init() {
        super.init()
        Machine.add(BoatArchitecture::class.java)
    }

    override fun registerModules(registry: IForgeRegistry<BoatModuleEntry>) {
        registry.registerModule(ComputerModule, ItemBlock.getItemFromBlock(Blocks.DEADBUSH))
    }

    @SideOnly(Side.CLIENT)
    override fun registerModuleRenderers(registry: IForgeRegistry<BoatModuleRenderer>) {
        registry.register(OCRenderer)
    }

    @SubscribeEvent
    fun attachCapabilities(evt: AttachCapabilitiesEvent<Entity>) {
        if(evt.`object` is ModularBoatEntity) {
            evt.addCapability(HostKey, MachineHostCapability(evt.`object` as ModularBoatEntity))
        }
    }

    @SubscribeEvent
    fun spyOCPackets(evt: FMLNetworkEvent.ClientCustomPacketEvent) {
        println("RECEIVED OC PACKET!!")
        val ocClass = Class.forName("li.cil.oc.common.ComponentTracker")
        val ocClientClass = Class.forName("li.cil.oc.client.ComponentTracker$")
        val compMethod = ocClass.getDeclaredMethod("components", World::class.java)
        compMethod.isAccessible = true
        val tracker = ocClientClass.getField("MODULE\$").get(null)
        val components: Cache<String, ManagedEnvironment> = compMethod.invoke(tracker, Minecraft.getMinecraft().world) as Cache<String, ManagedEnvironment>
        println("cache size = ${components.size()}")
        for((k, v) in components.asMap().entries) {
            println(">> '$k' = $v (${v.node()})")
        }
    }

}