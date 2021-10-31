package org.jglrxavpok.moarboats.common

import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.entities.utilityboats.*

object EntityEntries {

    private var ID = 0
    val ModularBoat = EntityType.Builder.of({ type: EntityType<ModularBoatEntity>, world -> ModularBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> ModularBoatEntity(u) }
            .build("modular_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "modular_boat")) as EntityType<ModularBoatEntity>

    val AnimalBoat = EntityType.Builder.of({ type: EntityType<AnimalBoatEntity>, world -> AnimalBoatEntity(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(1.375f *1.5f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> AnimalBoatEntity(u) }
            .build("animal_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "animal_boat")) as EntityType<AnimalBoatEntity>

    val FurnaceBoat = utilityBoatEntry("furnace", ::FurnaceBoatEntity)

    val SmokerBoat = utilityBoatEntry("smoker", ::SmokerBoatEntity)

    val BlastFurnaceBoat = utilityBoatEntry("blast_furnace", ::BlastFurnaceBoatEntity)

    val CraftingTableBoat = utilityBoatEntry("crafting_table", ::CraftingTableBoatEntity)

    val GrindstoneBoat = utilityBoatEntry("grindstone", ::GrindstoneBoatEntity)

    val StonecutterBoat = utilityBoatEntry("stonecutter", ::StonecutterBoatEntity)

    val LoomBoat = utilityBoatEntry("loom", ::LoomBoatEntity)

    val CartographyTableBoat = utilityBoatEntry("cartography_table", ::CartographyTableBoatEntity)

    val ChestBoat = utilityBoatEntry("chest", ::ChestBoatEntity)

    val EnderChestBoat = utilityBoatEntry("ender_chest", ::EnderChestBoatEntity)

    val ShulkerBoat = utilityBoatEntry("shulker", ::ShulkerBoatEntity)

    val JukeboxBoat = utilityBoatEntry("jukebox", ::JukeboxBoatEntity)

    val list = listOf(ModularBoat, AnimalBoat, FurnaceBoat, SmokerBoat, BlastFurnaceBoat,
            CraftingTableBoat, GrindstoneBoat, LoomBoat, CartographyTableBoat, StonecutterBoat, ChestBoat, EnderChestBoat, ShulkerBoat, JukeboxBoat)

    private inline fun <reified T: UtilityBoatEntity<*,*>> utilityBoatEntry(id: String, crossinline constructor: (World) -> T) = EntityType.Builder.of({ type: EntityType<T>, world -> constructor(world) }, EntityClassification.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { t, u -> constructor(u) }
            .build("${id}_boat")
            .setRegistryName(ResourceLocation(MoarBoats.ModID, "${id}_boat")) as EntityType<T>
}