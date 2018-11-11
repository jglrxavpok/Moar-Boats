package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.Machine
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.integration.MoarBoatsIntegration
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin
import org.jglrxavpok.moarboats.integration.opencomputers.architecture.BoatArchitecture

@MoarBoatsIntegration("opencomputers|core")
class OpenComputerPlugin: MoarBoatsPlugin {

    companion object {
        @CapabilityInject(MachineHostCapability::class)
        lateinit var HostCapability: Capability<MachineHostCapability>
        @JvmStatic
        val HostKey = ResourceLocation(MoarBoats.ModID, "opencomputer_host")
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
    }

    @SubscribeEvent
    fun attachCapabilities(evt: AttachCapabilitiesEvent<Entity>) {
        if(evt.`object` is ModularBoatEntity) {
            evt.addCapability(HostKey, MachineHostCapability(evt.`object` as ModularBoatEntity))
        }
    }
}