package org.jglrxavpok.moarboats.common

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityType.EntityFactory
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import net.minecraftforge.network.PlayMessages.SpawnEntity
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.entities.utilityboats.*

object EntityEntries {

    // vanilla: 1.375F
    private val BoatWidth = 1.375f

    @JvmField
    val Registry = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MoarBoats.ModID)

    @JvmField
    val ModularBoat = Registry.register("modular_boat") {
        val factory: EntityFactory<ModularBoatEntity> = EntityFactory<ModularBoatEntity>(::ModularBoatEntity)
        EntityType.Builder.of(factory, MobCategory.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(BoatWidth, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { packet, u -> ModularBoatEntity(packet, u) }
            .build("modular_boat")
    }

    @JvmField
    val AnimalBoat = Registry.register("animal_boat") {
        val factory: EntityFactory<AnimalBoatEntity> = EntityFactory<AnimalBoatEntity>(::AnimalBoatEntity)
        EntityType.Builder.of(factory, MobCategory.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(BoatWidth *1.5f, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { packet, u -> AnimalBoatEntity(packet, u) }
            .build("animal_boat")
    }

    @JvmField
    val FurnaceBoat = utilityBoatEntry("furnace", ::FurnaceBoatEntity, ::FurnaceBoatEntity)

    @JvmField
    val SmokerBoat = utilityBoatEntry("smoker", ::SmokerBoatEntity, ::SmokerBoatEntity)

    @JvmField
    val BlastFurnaceBoat = utilityBoatEntry("blast_furnace", ::BlastFurnaceBoatEntity, ::BlastFurnaceBoatEntity)

    @JvmField
    val CraftingTableBoat = utilityBoatEntry("crafting_table", ::CraftingTableBoatEntity, ::CraftingTableBoatEntity)

    @JvmField
    val GrindstoneBoat = utilityBoatEntry("grindstone", ::GrindstoneBoatEntity, ::GrindstoneBoatEntity)

    @JvmField
    val StonecutterBoat = utilityBoatEntry("stonecutter", ::StonecutterBoatEntity, ::StonecutterBoatEntity)

    @JvmField
    val LoomBoat = utilityBoatEntry("loom", ::LoomBoatEntity, ::LoomBoatEntity)

    @JvmField
    val CartographyTableBoat = utilityBoatEntry("cartography_table", ::CartographyTableBoatEntity, ::CartographyTableBoatEntity)

    @JvmField
    val ChestBoat = utilityBoatEntry("chest", ::ChestBoatEntity, ::ChestBoatEntity)

    @JvmField
    val EnderChestBoat = utilityBoatEntry("ender_chest", ::EnderChestBoatEntity, ::EnderChestBoatEntity)

    @JvmField
    val ShulkerBoat = utilityBoatEntry("shulker_box", ::ShulkerBoatEntity, ::ShulkerBoatEntity)

    @JvmField
    val JukeboxBoat = utilityBoatEntry("jukebox", ::JukeboxBoatEntity, ::JukeboxBoatEntity)

    private fun <T: UtilityBoatEntity<*,*>> utilityBoatEntry(id: String, constructor: (EntityType<T>, Level) -> T, clientFactory: (SpawnEntity, Level) -> T) = Registry.register("${id}_boat") {
        val factory = EntityFactory<T>(constructor)
        EntityType.Builder.of(factory, MobCategory.MISC)
            .setTrackingRange(64)
            .fireImmune()
            .sized(BoatWidth, 0.5625f)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(3)
            .setCustomClientFactory { packet, u -> clientFactory(packet, u) }
            .build("${id}_boat")
    }
}