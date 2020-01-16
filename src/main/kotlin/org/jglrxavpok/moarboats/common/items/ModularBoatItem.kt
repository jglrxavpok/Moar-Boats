package org.jglrxavpok.moarboats.common.items

import net.minecraft.entity.item.BoatEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.DyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.Stats
import net.minecraft.util.*
import net.minecraft.util.math.*
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.utilityboats.*
import org.jglrxavpok.moarboats.extensions.Fluids

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
        registryName = ResourceLocation(MoarBoats.ModID, "modular_boat_${dyeColor.translationKey}")
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        val color = dyeColor
        return ModularBoatEntity(
                levelIn,
                raytraceresult.hitVec.x,
                if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y,
                raytraceresult.hitVec.z,
                color,
                ModularBoatEntity.OwningMode.PlayerOwned,
                playerIn.gameProfile.id).apply {
                    readAdditional(itemstack.getOrCreateChildTag("boat_data"))
                }
    }

}

object AnimalBoatItem: BaseBoatItem() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "animal_boat")
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return AnimalBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z)
    }
}

class FurnaceBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "furnace") {

    companion object {
        val AllVersions = BoatType.values().map { FurnaceBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return FurnaceBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@FurnaceBoatItem.boatType }
    }
}

class SmokerBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "smoker") {

    companion object {
        val AllVersions = BoatType.values().map { SmokerBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return SmokerBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@SmokerBoatItem.boatType }
    }
}

class BlastFurnaceBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "blast_furnace") {

    companion object {
        val AllVersions = BoatType.values().map { BlastFurnaceBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return BlastFurnaceBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@BlastFurnaceBoatItem.boatType }
    }
}

class CraftingTableBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "crafting_table") {

    companion object {
        val AllVersions = BoatType.values().map { CraftingTableBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return CraftingTableBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@CraftingTableBoatItem.boatType }
    }

    override fun getContainerDisplayName(): ITextComponent {
        return TranslationTextComponent("container.crafting")
    }
}

class GrindstoneBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "grindstone") {

    companion object {
        val AllVersions = BoatType.values().map { GrindstoneBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return GrindstoneBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@GrindstoneBoatItem.boatType }
    }
}

class CartographyTableBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "cartography_table") {

    companion object {
        val AllVersions = BoatType.values().map { CartographyTableBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return CartographyTableBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@CartographyTableBoatItem.boatType }
    }
}

class LoomBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "loom") {

    companion object {
        val AllVersions = BoatType.values().map { LoomBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return LoomBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@LoomBoatItem.boatType }
    }
}

class StonecutterBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "stonecutter") {

    companion object {
        val AllVersions = BoatType.values().map { StonecutterBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return StonecutterBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@StonecutterBoatItem.boatType }
    }
}

class ChestBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "chest") {

    companion object {
        val AllVersions = BoatType.values().map { ChestBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return ChestBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@ChestBoatItem.boatType }
    }
}

class ShulkerBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "shulker") {

    companion object {
        val AllVersions = BoatType.values().map { ShulkerBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return ShulkerBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@ShulkerBoatItem.boatType }
    }

    override fun getContainerDisplayName(): ITextComponent {
        return TranslationTextComponent("container.shulkerBox")
    }
}

class EnderChestBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "ender_chest") {

    companion object {
        val AllVersions = BoatType.values().map { EnderChestBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return EnderChestBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@EnderChestBoatItem.boatType }
    }

    override fun getContainerDisplayName(): ITextComponent {
        return TranslationTextComponent("block.minecraft.ender_chest")
    }
}

class JukeboxBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "jukebox") {

    companion object {
        val AllVersions = BoatType.values().map { JukeboxBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return JukeboxBoatEntity(levelIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z).apply { boatType = this@JukeboxBoatItem.boatType }
    }

    override fun getContainerDisplayName(): ITextComponent {
        return TranslationTextComponent("block.minecraft.jukebox")
    }
}

abstract class UtilityBoatItem(val boatType: BoatType, val containerType: String): BaseBoatItem() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "${boatType.getName()}_${containerType}_boat")
    }

    open fun getContainerDisplayName(): ITextComponent = TranslationTextComponent("container.$containerType")

    override fun getDisplayName(stack: ItemStack): ITextComponent {
        return TranslationTextComponent("item.moarboats.utility_boat.name", TranslationTextComponent("item.${boatType.getOriginModID()}.${boatType.getName()}_boat"), getContainerDisplayName())
    }
}

abstract class BaseBoatItem: Item(Item.Properties().group(MoarBoats.CreativeTab).maxStackSize(1)) {

    override fun onItemRightClick(levelIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        val itemstack = playerIn.getHeldItem(handIn)
        val f1 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * 1.0f
        val f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * 1.0f
        val d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * 1.0
        val d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * 1.0 + playerIn.getEyeHeight().toDouble()
        val d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * 1.0
        val vec3d = Vec3d(d0, d1, d2)
        val f3 = MathHelper.cos(-f2 * 0.017453292f - Math.PI.toFloat())
        val f4 = MathHelper.sin(-f2 * 0.017453292f - Math.PI.toFloat())
        val f5 = -MathHelper.cos(-f1 * 0.017453292f)
        val f6 = MathHelper.sin(-f1 * 0.017453292f)
        val f7 = f4 * f5
        val f8 = f3 * f5
        val vec3d1 = vec3d.add(f7.toDouble() * 5.0, f6.toDouble() * 5.0, f8.toDouble() * 5.0)
        val raytraceresult = levelIn.rayTraceBlocks(RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, playerIn))

        if (raytraceresult == null) {
            return ActionResult(ActionResultType.PASS, itemstack)
        } else {
            val vec3d2 = playerIn.getLook(1.0f)
            val list = levelIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.boundingBox.expand(vec3d2.x * 5.0, vec3d2.y * 5.0, vec3d2.z * 5.0).grow(1.0))

            val flag = list.indices
                    .map { list[it] }
                    .filter { it.canBeCollidedWith() }
                    .map { it.boundingBox.grow(it.collisionBorderSize.toDouble()) }
                    .any { it.contains(vec3d) }

            if (flag) {
                return ActionResult(ActionResultType.PASS, itemstack)
            } else if (raytraceresult.type != RayTraceResult.Type.BLOCK) {
                return ActionResult(ActionResultType.PASS, itemstack)
            } else {
                val inUsualFluid = Fluids.isUsualLiquidBlock(levelIn, raytraceresult.pos)
                val entityboat = createBoat(levelIn, raytraceresult, inUsualFluid, itemstack, playerIn)
                entityboat.rotationYaw = playerIn.rotationYaw

                return if (levelIn.getCollisionShapes(entityboat, entityboat.boundingBox.grow(-0.1)).count() != 0L) {
                    ActionResult(ActionResultType.FAIL, itemstack)
                } else {
                    if (!levelIn.isRemote) {
                        levelIn.addEntity(entityboat)
                    }

                    if (!playerIn.isCreative) {
                        itemstack.shrink(1)
                    }

                    playerIn.addStat(Stats.ITEM_USED[this])
                    ActionResult(ActionResultType.SUCCESS, itemstack)
                }
            }
        }
    }

    abstract fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity
}