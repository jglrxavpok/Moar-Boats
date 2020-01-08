package org.jglrxavpok.moarboats.common

import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.utilityboats.*

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

    val FurnaceBoat = EntityType.Builder.create({ type: EntityType<FurnaceBoatEntity>, world -> FurnaceBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> FurnaceBoatEntity(u) }
            .build("furnace_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "furnace_boat")) as EntityType<FurnaceBoatEntity>

    val SmokerBoat = EntityType.Builder.create({ type: EntityType<SmokerBoatEntity>, world -> SmokerBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> SmokerBoatEntity(u) }
            .build("smoker_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "smoker_boat")) as EntityType<SmokerBoatEntity>

    val BlastFurnaceBoat = EntityType.Builder.create({ type: EntityType<BlastFurnaceBoatEntity>, world -> BlastFurnaceBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> BlastFurnaceBoatEntity(u) }
            .build("blast_furnace_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "blast_furnace_boat")) as EntityType<BlastFurnaceBoatEntity>

    val CraftingTableBoat = EntityType.Builder.create({ type: EntityType<CraftingTableBoatEntity>, world -> CraftingTableBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> CraftingTableBoatEntity(u) }
            .build("crafting_table_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "crafting_table_boat")) as EntityType<CraftingTableBoatEntity>

    val GrindstoneBoat = EntityType.Builder.create({ type: EntityType<GrindstoneBoatEntity>, world -> GrindstoneBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> GrindstoneBoatEntity(u) }
            .build("grindstone_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "grindstone_boat")) as EntityType<GrindstoneBoatEntity>

    val LoomBoat = EntityType.Builder.create({ type: EntityType<LoomBoatEntity>, world -> LoomBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> LoomBoatEntity(u) }
            .build("loom_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "loom_boat")) as EntityType<LoomBoatEntity>

    val CartographyTableBoat = EntityType.Builder.create({ type: EntityType<CartographyTableBoatEntity>, world -> CartographyTableBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .immuneToFire()
            .size(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> CartographyTableBoatEntity(u) }
            .build("cartography_table_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "cartography_table_boat")) as EntityType<CartographyTableBoatEntity>

    val list = listOf(ModularBoat, AnimalBoat, FurnaceBoat, SmokerBoat, BlastFurnaceBoat, CraftingTableBoat, GrindstoneBoat, LoomBoat, CartographyTableBoat)
}