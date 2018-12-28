package org.jglrxavpok.moarboats.integration.opencomputers

import org.apache.commons.lang3.tuple.Pair as Tuple
import li.cil.oc.api.IMC
import li.cil.oc.api.Machine
import li.cil.oc.api.driver.item.Slot
import li.cil.oc.api.machine.Arguments
import li.cil.oc.api.machine.Callback
import li.cil.oc.api.machine.Context
import li.cil.oc.api.network.Node
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.*
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.integration.MoarBoatsIntegration
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin
import org.jglrxavpok.moarboats.integration.opencomputers.client.OCRenderer
import org.jglrxavpok.moarboats.integration.opencomputers.items.ModuleHolderItem
import org.jglrxavpok.moarboats.integration.opencomputers.network.SSyncMachineData

@MoarBoatsIntegration("opencomputers|core")
class OpenComputerPlugin: MoarBoatsPlugin {

    companion object {
        @CapabilityInject(MachineHostCapability::class)
        @JvmStatic
        lateinit var HostCapability: Capability<MachineHostCapability>

        @JvmStatic
        val HostKey = ResourceLocation(MoarBoats.ModID, "opencomputer_host")
        val ModuleValueSuppliers = mutableMapOf<BoatModule, (IControllable, BoatModule) -> ModuleValue>()

        fun registerModuleValueSupplier(module: BoatModule, supplier: (IControllable, BoatModule) -> ModuleValue) {
            ModuleValueSuppliers += module to supplier
        }

        fun getHost(boat: IControllable): BoatMachineHost? = boat.correspondingEntity.getCapability(HostCapability, null)?.host

        fun createModuleValue(boat: IControllable, module: BoatModule): ModuleValue {
            if(module in ModuleValueSuppliers)
                return ModuleValueSuppliers[module]!!(boat, module)
            return ModuleValue(module)
        }
    }

    override fun handlers() = listOf(SSyncMachineData.Handler)

    override fun preInit() {
        super.preInit()
        IMC.registerAssemblerTemplate("moarboats:module_assemble",
                "org.jglrxavpok.moarboats.integration.opencomputers.ModuleTemplate.select",
                "org.jglrxavpok.moarboats.integration.opencomputers.ModuleTemplate.validate",
                "org.jglrxavpok.moarboats.integration.opencomputers.ModuleTemplate.assemble",
                BoatMachineHost::class.java, intArrayOf(0).apply { fill(3) }, intArrayOf(0),
                arrayListOf(
                        Tuple.of(Slot.CPU, 3),
                        Tuple.of(Slot.HDD, 3),
                        Tuple.of(Slot.Memory, 3),
                        Tuple.of(Slot.Card, 0),
                        Tuple.of(Slot.Floppy, 3),
                        Tuple.of("eeprom", 3)
                ))
        IMC.registerDisassemblerTemplate("moarboats:module_disassemble",
                "org.jglrxavpok.moarboats.integration.opencomputers.ModuleTemplate.selectDisassembly",
                "org.jglrxavpok.moarboats.integration.opencomputers.ModuleTemplate.disassemble")
        registerAsEventSubscriber()
        CapabilityManager.INSTANCE.register(MachineHostCapability::class.java, MachineHostCapability.Storage) {throw UnsupportedOperationException()}
    }

    @SubscribeEvent
    fun registerItems(evt: RegistryEvent.Register<Item>) {
        evt.registry.register(ModuleHolderItem)
    }

    override fun registerModules(registry: IForgeRegistry<BoatModuleEntry>) {
        registry.registerModule(ComputerModule, ModuleHolderItem)

        registerModuleValueSupplier(ChestModule, ::StorageModuleValue)
        registerModuleValueSupplier(DispenserModule, ::StorageModuleValue)
        registerModuleValueSupplier(DropperModule, ::StorageModuleValue)
        registerModuleValueSupplier(FurnaceEngineModule, ::EngineModuleValue)
        registerModuleValueSupplier(SolarEngineModule, ::EngineModuleValue)
        registerModuleValueSupplier(CreativeEngineModule, ::EngineModuleValue)
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