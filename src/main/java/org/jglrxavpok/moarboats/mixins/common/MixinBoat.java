package org.jglrxavpok.moarboats.mixins.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.Boat;
import org.jglrxavpok.moarboats.api.Cleat;
import org.jglrxavpok.moarboats.api.Link;
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
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Boat.class)
public class MixinBoat implements ICleatLinkStorage {

    private Boat self() {
        return (Boat)(Object)this;
    }

    private ICleatCapability getCleatCapability() {
        return self().getCapability(ICleatCapability.Companion.getCapability()).orElseThrow(() -> new IllegalStateException("No cleat capability on this boat"));
    }

    @Override
    public ConcurrentHashMap<Cleat, Link> getLinkStorage() {
        return self().getEntityData().get(StandaloneCleat.BOAT_LINKS);
    }

    @Override
    public void syncLinkStorage(Map<Cleat, Link> newValue) {
        newValue = new HashMap<Cleat, Link>(newValue); // handle self assign
        Map<Cleat, Link> links = getLinkStorage();
        links.clear();
        links.putAll(newValue);
        EntityExtensionsKt.setDirty(self().getEntityData(), StandaloneCleat.BOAT_LINKS);
    }

    @Inject(at = @At("HEAD"), method = "defineSynchedData()V")
    public void defineSynchedData(CallbackInfo ci) {
        self().getEntityData().define(StandaloneCleat.BOAT_LINKS, new ConcurrentHashMap<>());
    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo ci) {
        getCleatCapability().tick(self().level, self());
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        getCleatCapability().readFromNBT(nbt);
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        getCleatCapability().saveToNBT(nbt);
    }

}
