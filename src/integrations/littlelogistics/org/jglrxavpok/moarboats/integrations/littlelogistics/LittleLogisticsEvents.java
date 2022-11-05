package org.jglrxavpok.moarboats.integrations.littlelogistics;

import dev.murad.shipping.entity.custom.vessel.VesselEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.moarboats.api.Cleat;
import org.jglrxavpok.moarboats.api.Link;
import org.jglrxavpok.moarboats.common.BoatLinksSerializer;
import org.jglrxavpok.moarboats.common.Cleats;
import org.jglrxavpok.moarboats.common.EntityEntries;
import org.jglrxavpok.moarboats.common.entities.StandaloneCleat;
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatCapability;
import org.jglrxavpok.moarboats.extensions.EntityExtensionsKt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LittleLogisticsEvents {

    private static final EntityDataAccessor<ConcurrentHashMap<Cleat, Link>> BOAT_LINKS = SynchedEntityData.defineId(VesselEntity.class, BoatLinksSerializer.INSTANCE);

    @SubscribeEvent
    public static void injectCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof VesselEntity entity) {
            event.addCapability(ICleatCapability.ResourceID, new ICleatCapability() {
                @NotNull
                @Override
                public ConcurrentHashMap<Cleat, Link> getLinkStorage() {
                    return entity.getEntityData().get(BOAT_LINKS);
                }

                @Override
                public void syncLinkStorage(@NotNull Map<Cleat, Link> newValue) {
                    newValue = new HashMap<Cleat, Link>(newValue); // handle self assign
                    Map<Cleat, Link> links = getLinkStorage();
                    links.clear();
                    links.putAll(newValue);
                    EntityExtensionsKt.setDirty(entity.getEntityData(), BOAT_LINKS);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof VesselEntity entity && !event.getLevel().isClientSide) {
            event.getLevel().addFreshEntity(new StandaloneCleat(EntityEntries.StandaloneCleat.get(), event.getLevel(), Cleats.FrontCleat, entity));
            event.getLevel().addFreshEntity(new StandaloneCleat(EntityEntries.StandaloneCleat.get(), event.getLevel(), Cleats.BackCleat, entity));
        }
    }

    @SubscribeEvent
    public static void onEntityConstruct(EntityEvent.EntityConstructing event) {
        if(event.getEntity() instanceof VesselEntity) {
            event.getEntity().getEntityData().define(BOAT_LINKS, new ConcurrentHashMap<>());
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if(event.getEntity() instanceof VesselEntity) {
            event.getEntity().getCapability(ICleatCapability.Capability).ifPresent(cleatCapability -> {
                cleatCapability.tick(event.getEntity().getLevel(), event.getEntity());
            });
        }
    }
}
