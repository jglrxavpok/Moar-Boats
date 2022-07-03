package org.jglrxavpok.moarboats.common

import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.NewRegistryEvent
import net.minecraftforge.registries.ObjectHolder
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.registries.RegistryBuilder
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.Cleat
import java.util.function.Supplier

@Mod.EventBusSubscriber(modid = MoarBoats.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
object Cleats {

    lateinit var Registry: Supplier<IForgeRegistry<Cleat>>

    val RegistryKey = ResourceKey.createRegistryKey<Cleat>(ResourceLocation(MoarBoats.ModID, "cleats"))

    @ObjectHolder(value = MoarBoats.ModID+":basic_towed", registryName = MoarBoats.ModID+":cleats")
    lateinit var FrontCleat: Cleat

    @ObjectHolder(value = MoarBoats.ModID+":basic_towing", registryName = MoarBoats.ModID+":cleats")
    lateinit var BackCleat: Cleat

    @SubscribeEvent
    fun onNewRegistry(event: NewRegistryEvent) {
        Registry = event.create(
            RegistryBuilder<Cleat>()
            .allowModification()
            .setName(RegistryKey.location())
        )
    }

    @SubscribeEvent
    fun register(event: RegisterEvent) {
        event.register(Cleats.RegistryKey) { helper ->
            helper.register("basic_towed", BasicCleat(false))
            helper.register("basic_towing", BasicCleat(true))
        }
    }
}