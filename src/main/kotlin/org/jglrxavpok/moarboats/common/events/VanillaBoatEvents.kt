package org.jglrxavpok.moarboats.common.events

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.vehicle.Boat
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.api.Link
import org.jglrxavpok.moarboats.common.Cleats
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.entities.StandaloneCleat
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatCapability
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatLinkStorage
import org.jglrxavpok.moarboats.mixins.common.MixinBoat


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = MoarBoats.ModID)
object VanillaBoatEvents {

    @SubscribeEvent
    fun injectCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        // called first (before spawn)
        if(event.`object` is Boat) {
            event.addCapability(ICleatCapability.ResourceID, object: ICleatCapability() {

                private fun withMixin(): ICleatLinkStorage {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    return event.`object` as ICleatLinkStorage
                }

                override fun getLinkStorage(): MutableMap<Cleat, Link> {
                    return withMixin().getLinkStorage()
                }

                override fun syncLinkStorage(newValue: MutableMap<Cleat, Link>) {
                    return withMixin().syncLinkStorage(newValue)
                }

            });
        }
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