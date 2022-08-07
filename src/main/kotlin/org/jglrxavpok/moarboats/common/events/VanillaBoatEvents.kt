package org.jglrxavpok.moarboats.common.events

import net.minecraft.world.entity.vehicle.Boat
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.BasicCleat
import org.jglrxavpok.moarboats.common.Cleats
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.entities.StandaloneCleat
import org.jglrxavpok.moarboats.common.vanillaglue.CleatCapability

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = MoarBoats.ModID)
object VanillaBoatEvents {

    @SubscribeEvent
    fun injectCapabilities(event: AttachCapabilitiesEvent<Boat>) {
        // called first (before spawn)
        event.addCapability(CleatCapability.ResourceID, CleatCapability())
    }

    @SubscribeEvent
    fun onEntitySpawn(event: EntityJoinLevelEvent) {
        if(event.entity is Boat && !event.level.isClientSide) {
            event.level.addFreshEntity(StandaloneCleat(EntityEntries.StandaloneCleat.get(), event.level, Cleats.FrontCleat, event.entity))
            event.level.addFreshEntity(StandaloneCleat(EntityEntries.StandaloneCleat.get(), event.level, Cleats.BackCleat, event.entity))
        }
    }

    // tick handling done in mixin

}