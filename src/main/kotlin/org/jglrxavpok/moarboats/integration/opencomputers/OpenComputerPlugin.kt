package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.Machine
import li.cil.oc.api.network.Node
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.api.registerModule
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.integration.MoarBoatsIntegration
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin
import org.jglrxavpok.moarboats.integration.opencomputers.architecture.BoatArchitecture
import org.jglrxavpok.moarboats.integration.opencomputers.client.OCRenderer

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

    override fun preInit() {
        super.preInit()
        registerAsEventSubscriber()
        CapabilityManager.INSTANCE.register(MachineHostCapability::class.java, MachineHostCapability.Storage) {throw UnsupportedOperationException()};
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

}