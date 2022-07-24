package org.jglrxavpok.moarboats.common.events

import net.minecraft.world.entity.vehicle.Boat
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.jglrxavpok.moarboats.MoarBoats

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = MoarBoats.ModID)
object VanillaBoatEvents {

    @SubscribeEvent
    fun injectCapabilities(event: AttachCapabilitiesEvent<Boat>) {
        // called first (before spawn)
        // TODO: event.addCapability()
    }

    @SubscribeEvent
    fun onEntitySpawn(event: EntityJoinLevelEvent) {
        if(event.entity is Boat && !event.level.isClientSide) {
            // TODO: add cleat entities
        }
    }

    // tick handling done in mixin

}