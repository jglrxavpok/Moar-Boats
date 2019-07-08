package org.jglrxavpok.moarboats.common

import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

object EntityEntries {

    private var ID = 0
    val ModularBoat = EntityType.Builder.create(ModularBoatEntity::class.java, ::ModularBoatEntity)
            .tracker(64, 3, true)
            .build("moarboats.modular_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "modular_boat")) as EntityType<ModularBoatEntity>
    val AnimalBoat = EntityType.Builder.create(AnimalBoatEntity::class.java, ::AnimalBoatEntity)
            .tracker(64, 3, true)
            .build("moarboats.animal_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "animal_boat")) as EntityType<AnimalBoatEntity>
    val list = listOf(ModularBoat, AnimalBoat)
}