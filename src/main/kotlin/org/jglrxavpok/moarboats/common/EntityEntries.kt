package org.jglrxavpok.moarboats.common

import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

object EntityEntries {

    private var ID = 0
    val ModularBoat = EntityType.Builder.create({ type: EntityType<ModularBoatEntity>, world -> ModularBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> ModularBoatEntity(u) }
            .build("modular_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "modular_boat")) as EntityType<ModularBoatEntity>

    val AnimalBoat = EntityType.Builder.create({ type: EntityType<AnimalBoatEntity>, world -> AnimalBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f *1.5f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> AnimalBoatEntity(u) }
            .build("animal_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "animal_boat")) as EntityType<AnimalBoatEntity>
    val list = listOf(ModularBoat, AnimalBoat)
}