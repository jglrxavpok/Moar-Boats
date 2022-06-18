package org.jglrxavpok.moarboats.common.items

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stats
import net.minecraft.util.*
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.utilityboats.*
import org.jglrxavpok.moarboats.extensions.Fluids
import java.util.*

class ModularBoatItem(val dyeColor: DyeColor): BaseBoatItem() {

    companion object {
        val White = ModularBoatItem(DyeColor.WHITE)
        val Orange = ModularBoatItem(DyeColor.ORANGE)
        val Magenta = ModularBoatItem(DyeColor.MAGENTA)
        val LightBlue = ModularBoatItem(DyeColor.LIGHT_BLUE)
        val Yellow = ModularBoatItem(DyeColor.YELLOW)
        val Lime = ModularBoatItem(DyeColor.LIME)
        val Pink = ModularBoatItem(DyeColor.PINK)
        val Gray = ModularBoatItem(DyeColor.GRAY)
        val LightGray = ModularBoatItem(DyeColor.LIGHT_GRAY)
        val Cyan = ModularBoatItem(DyeColor.CYAN)
        val Purple = ModularBoatItem(DyeColor.PURPLE)
        val Blue = ModularBoatItem(DyeColor.BLUE)
        val Brown = ModularBoatItem(DyeColor.BROWN)
        val Green = ModularBoatItem(DyeColor.GREEN)
        val Red = ModularBoatItem(DyeColor.RED)
        val Black = ModularBoatItem(DyeColor.BLACK)
        // in same order as DyeColor
        val AllVersions = arrayOf(
                White,
                Orange,
                Magenta,
                LightBlue,
                Yellow,
                Lime,
                Pink,
                Gray,
                LightGray,
                Cyan,
                Purple,
                Blue,
                Brown,
                Green,
                Red,
                Black
        )

        operator fun get(color: DyeColor): ModularBoatItem {
            return AllVersions[color.ordinal]
        }
    }

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "modular_boat_${dyeColor.getName()}")
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        val color = dyeColor
        return ModularBoatEntity(
                levelIn,
                raytraceresult.location.x,
                if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y,
                raytraceresult.location.z,
                color,
                ModularBoatEntity.OwningMode.PlayerOwned,
                playerIn.gameProfile.id).apply {
                    readAdditionalSaveData(itemstack.getOrCreateTagElement("boat_data"))
                }
    }

}

object AnimalBoatItem: BaseBoatItem() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "animal_boat")
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return AnimalBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z)
    }
}

class FurnaceBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "furnace") {

    companion object {
        val AllVersions = BoatType.values().map { FurnaceBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return FurnaceBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@FurnaceBoatItem.boatType }
    }
}

class SmokerBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "smoker") {

    companion object {
        val AllVersions = BoatType.values().map { SmokerBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return SmokerBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@SmokerBoatItem.boatType }
    }
}

class BlastFurnaceBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "blast_furnace") {

    companion object {
        val AllVersions = BoatType.values().map { BlastFurnaceBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return BlastFurnaceBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@BlastFurnaceBoatItem.boatType }
    }
}

class CraftingTableBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "crafting_table") {

    companion object {
        val AllVersions = BoatType.values().map { CraftingTableBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return CraftingTableBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@CraftingTableBoatItem.boatType }
    }

    override fun getContainerDisplayName(): Component {
        return Component.translatable("container.crafting")
    }
}

class GrindstoneBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "grindstone") {

    companion object {
        val AllVersions = BoatType.values().map { GrindstoneBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return GrindstoneBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@GrindstoneBoatItem.boatType }
    }

    override fun getName(stack: ItemStack): Component {
        return Component.translatable("item.moarboats.utility_boat.name", Component.translatable("item.${boatType.getBaseBoatOriginModID()}.${boatType.getShortName()}_boat"), Component.translatable("block.minecraft.grindstone"))
    }
}

class CartographyTableBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "cartography_table") {

    companion object {
        val AllVersions = BoatType.values().map { CartographyTableBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return CartographyTableBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@CartographyTableBoatItem.boatType }
    }
}

class LoomBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "loom") {

    companion object {
        val AllVersions = BoatType.values().map { LoomBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return LoomBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@LoomBoatItem.boatType }
    }
}

class StonecutterBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "stonecutter") {

    companion object {
        val AllVersions = BoatType.values().map { StonecutterBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return StonecutterBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@StonecutterBoatItem.boatType }
    }
}

class ChestBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "chest") {

    companion object {
        val AllVersions = BoatType.values().map { ChestBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return ChestBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@ChestBoatItem.boatType }
    }
}

class ShulkerBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "shulker") {

    companion object {
        val AllVersions = BoatType.values().map { ShulkerBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return ShulkerBoatEntity(DyeColor.byName(itemstack.getOrCreateTagElement("AdditionalData").getString("Color"), null), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z)
                .apply { boatType = this@ShulkerBoatItem.boatType }
                .apply { getBackingTileEntity()!!.deserializeNBT(itemstack.getOrCreateTagElement("AdditionalData").getCompound("TileEntityData")) }
    }

    override fun getContainerDisplayName(): Component {
        return Component.translatable("container.shulkerBox")
    }

    override fun getName(stack: ItemStack): Component {
        val color = DyeColor.byName(stack.getOrCreateTagElement("AdditionalData").getString("Color"), null)
        if(color != null) {
            return Component.translatable("item.moarboats.colored_utility_boat.name", Component.translatable("item.${boatType.getBaseBoatOriginModID()}.${boatType.getShortName()}_boat"), getContainerDisplayName(), Component.translatable("color.minecraft.${color.getName()}"))
        }
        return super.getName(stack)
    }
}

class EnderChestBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "ender_chest") {

    companion object {
        val AllVersions = BoatType.values().map { EnderChestBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return EnderChestBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@EnderChestBoatItem.boatType }
    }

    override fun getContainerDisplayName(): Component {
        return Component.translatable("block.minecraft.ender_chest")
    }
}

class JukeboxBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "jukebox") {

    companion object {
        val AllVersions = BoatType.values().map { JukeboxBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return JukeboxBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@JukeboxBoatItem.boatType }
    }

    override fun getContainerDisplayName(): Component {
        return Component.translatable("block.minecraft.jukebox")
    }
}

abstract class UtilityBoatItem(val boatType: BoatType, val containerType: String): BaseBoatItem({ tab(MoarBoats.UtilityBoatTab) }) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "${boatType.getFullName()}_${containerType}_boat")
    }

    open fun getContainerDisplayName(): Component = Component.translatable("container.$containerType")

    override fun getName(stack: ItemStack): Component {
        return Component.translatable("item.moarboats.utility_boat.name", Component.translatable("item.${boatType.getBaseBoatOriginModID()}.${boatType.getShortName()}_boat"), getContainerDisplayName())
    }
}

abstract class BaseBoatItem(propertiesModifier: Item.Properties.() -> Unit = {}): Item(Item.Properties().tab(MoarBoats.MainCreativeTab).stacksTo(1).also(propertiesModifier)) {

    override fun use(levelIn: Level, playerIn: Player, handIn: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemstack = playerIn.getItemInHand(handIn)
        val f1 = playerIn.xRotO + (playerIn.xRot - playerIn.xRotO) * 1.0f
        val f2 = playerIn.yRotO + (playerIn.yRot - playerIn.yRotO) * 1.0f
        val d0 = playerIn.xOld + (playerIn.x - playerIn.xOld) * 1.0
        val d1 = playerIn.yOld + (playerIn.y - playerIn.yOld) * 1.0 + playerIn.getEyeHeight().toDouble()
        val d2 = playerIn.zOld + (playerIn.z - playerIn.zOld) * 1.0
        val vec3d = Vec3(d0, d1, d2)
        val f3 = Mth.cos(-f2 * 0.017453292f - Math.PI.toFloat())
        val f4 = Mth.sin(-f2 * 0.017453292f - Math.PI.toFloat())
        val f5 = -Mth.cos(-f1 * 0.017453292f)
        val f6 = Mth.sin(-f1 * 0.017453292f)
        val f7 = f4 * f5
        val f8 = f3 * f5
        val vec3d1 = vec3d.add(f7.toDouble() * 5.0, f6.toDouble() * 5.0, f8.toDouble() * 5.0)
        val raytraceresult = levelIn.clip(ClipContext(vec3d, vec3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, playerIn))

        if (raytraceresult == null) {
            return InteractionResultHolder.pass(itemstack)
        } else {
            val vec3d2 = playerIn.getViewVector(1.0f)
            val list = levelIn.getEntities(playerIn, playerIn.boundingBox.expandTowards(vec3d2.x * 5.0, vec3d2.y * 5.0, vec3d2.z * 5.0).inflate(1.0))

            val flag = list.indices
                    .map { list[it] }
                    .filter { it.canBeCollidedWith() }
                    .map { it.boundingBox.inflate(it.pickRadius.toDouble()) }
                    .any { it.contains(vec3d) }

            if (flag) {
                return InteractionResultHolder(InteractionResult.PASS, itemstack)
            } else if (raytraceresult.type != HitResult.Type.BLOCK) {
                return InteractionResultHolder(InteractionResult.PASS, itemstack)
            } else {
                val inUsualFluid = Fluids.isUsualLiquidBlock(levelIn, raytraceresult.blockPos)
                val entityboat = createBoat(levelIn, raytraceresult, inUsualFluid, itemstack, playerIn)
                entityboat.yRot = playerIn.yRot

                return if (levelIn.getBlockCollisions(entityboat, entityboat.boundingBox.inflate(-0.1)).isNotEmpty()) {
                    InteractionResultHolder(InteractionResult.FAIL, itemstack)
                } else {
                    if (!levelIn.isClientSide) {
                        levelIn.addFreshEntity(entityboat)
                    }

                    if (!playerIn.isCreative) {
                        itemstack.shrink(1)
                    }

                    playerIn.awardStat(Stats.ITEM_USED[this])
                    InteractionResultHolder(InteractionResult.SUCCESS, itemstack)
                }
            }
        }
    }

    abstract fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity
}