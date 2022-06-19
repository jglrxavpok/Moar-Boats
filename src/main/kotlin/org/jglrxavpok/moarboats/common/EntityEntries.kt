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

    @JvmField
    val Registry = DeferredRegister.create(ForgeRegistries.ENTITIES, MoarBoats.ModID)

    @JvmField
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

    @JvmField
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

    @JvmField
    val FurnaceBoat = utilityBoatEntry("furnace", ::FurnaceBoatEntity)

    @JvmField
    val SmokerBoat = utilityBoatEntry("smoker", ::SmokerBoatEntity)

    @JvmField
    val BlastFurnaceBoat = utilityBoatEntry("blast_furnace", ::BlastFurnaceBoatEntity)

    @JvmField
    val CraftingTableBoat = utilityBoatEntry("crafting_table", ::CraftingTableBoatEntity)

    @JvmField
    val GrindstoneBoat = utilityBoatEntry("grindstone", ::GrindstoneBoatEntity)

    @JvmField
    val StonecutterBoat = utilityBoatEntry("stonecutter", ::StonecutterBoatEntity)

    @JvmField
    val LoomBoat = utilityBoatEntry("loom", ::LoomBoatEntity)

    @JvmField
    val CartographyTableBoat = utilityBoatEntry("cartography_table", ::CartographyTableBoatEntity)

    @JvmField
    val ChestBoat = utilityBoatEntry("chest", ::ChestBoatEntity)

    @JvmField
    val EnderChestBoat = utilityBoatEntry("ender_chest", ::EnderChestBoatEntity)

    @JvmField
    val ShulkerBoat = utilityBoatEntry("shulker", ::ShulkerBoatEntity)

    @JvmField
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