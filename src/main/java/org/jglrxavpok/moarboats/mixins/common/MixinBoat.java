package org.jglrxavpok.moarboats.mixins.common;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.vehicle.Boat;
import org.jglrxavpok.moarboats.api.Cleat;
import org.jglrxavpok.moarboats.api.Link;
import org.jglrxavpok.moarboats.common.BoatLinksSerializer;
import org.jglrxavpok.moarboats.common.entities.StandaloneCleat;
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatCapability;
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatLinkStorage;
import org.jglrxavpok.moarboats.extensions.EntityExtensionsKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Boat.class)
public class MixinBoat implements ICleatLinkStorage {

    private Boat self() {
        return (Boat)(Object)this;
    }

    @Override
    public Map<Cleat, Link> getLinkStorage() {
        return self().getEntityData().get(StandaloneCleat.BOAT_LINKS);
    }

    @Override
    public void syncLinkStorage(Map<Cleat, Link> newValue) {
        Map<Cleat, Link> links = getLinkStorage();
        links.clear();
        links.putAll(newValue);
        EntityExtensionsKt.setDirty(self().getEntityData(), StandaloneCleat.BOAT_LINKS);
    }

    @Inject(at = @At("HEAD"), method = "defineSynchedData()V")
    public void defineSynchedData(CallbackInfo ci) {
        self().getEntityData().define(StandaloneCleat.BOAT_LINKS, new HashMap<>());
    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo ci) {
        ICleatCapability capability = self().getCapability(ICleatCapability.Companion.getCapability()).orElseThrow(() -> new IllegalStateException("No cleat capability on this boat"));
        capability.tick(self().level, self());
    }

    // TODO: NBT saving/loading

}
