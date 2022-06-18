package org.jglrxavpok.moarboats.common

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityType.EntityFactory
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.entities.utilityboats.*

object EntityEntries {

    val Registry = DeferredRegister.create(ForgeRegistries.ENTITIES, MoarBoats.ModID)

    val ModularBoat = Registry.register("modular_boat") {
        val factory: EntityFactory<ModularBoatEntity> = EntityFactory<ModularBoatEntity>(::ModularBoatEntity)
        EntityType.Builder.of(factory, MobCategory.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .build("modular_boat")
    }

    val AnimalBoat = Registry.register("animal_boat") {
        val factory: EntityFactory<AnimalBoatEntity> = EntityFactory<AnimalBoatEntity>(::AnimalBoatEntity)
        EntityType.Builder.of(factory, MobCategory.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(1.375f *1.5f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .build("animal_boat")
    }

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

    private fun <T: UtilityBoatEntity<*,*>> utilityBoatEntry(id: String, constructor: (EntityType<T>, Level) -> T) = Registry.register(id) {
        val factory = EntityFactory<T>(constructor)
        EntityType.Builder.of(factory, MobCategory.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(1.375f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .build(id)
    }
}