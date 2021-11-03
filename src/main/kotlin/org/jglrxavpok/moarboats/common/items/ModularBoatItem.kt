package org.jglrxavpok.moarboats.common.items

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.DyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.Stats
import net.minecraft.util.*
import net.minecraft.util.math.*
import net.minecraft.util.math.vector.Vector3d
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

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
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

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return AnimalBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z)
    }
}

class FurnaceBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "furnace") {

    companion object {
        val AllVersions = BoatType.values().map { FurnaceBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return FurnaceBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@FurnaceBoatItem.boatType }
    }
}

class SmokerBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "smoker") {

    companion object {
        val AllVersions = BoatType.values().map { SmokerBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return SmokerBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@SmokerBoatItem.boatType }
    }
}

class BlastFurnaceBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "blast_furnace") {

    companion object {
        val AllVersions = BoatType.values().map { BlastFurnaceBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return BlastFurnaceBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@BlastFurnaceBoatItem.boatType }
    }
}

class CraftingTableBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "crafting_table") {

    companion object {
        val AllVersions = BoatType.values().map { CraftingTableBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return CraftingTableBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@CraftingTableBoatItem.boatType }
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
        return GrindstoneBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@GrindstoneBoatItem.boatType }
    }

    override fun getName(stack: ItemStack): ITextComponent {
        return TranslationTextComponent("item.moarboats.utility_boat.name", TranslationTextComponent("item.${boatType.getBaseBoatOriginModID()}.${boatType.getShortName()}_boat"), TranslationTextComponent("block.minecraft.grindstone"))
    }
}

class CartographyTableBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "cartography_table") {

    companion object {
        val AllVersions = BoatType.values().map { CartographyTableBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return CartographyTableBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@CartographyTableBoatItem.boatType }
    }
}

class LoomBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "loom") {

    companion object {
        val AllVersions = BoatType.values().map { LoomBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return LoomBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@LoomBoatItem.boatType }
    }
}

class StonecutterBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "stonecutter") {

    companion object {
        val AllVersions = BoatType.values().map { StonecutterBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return StonecutterBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@StonecutterBoatItem.boatType }
    }
}

class ChestBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "chest") {

    companion object {
        val AllVersions = BoatType.values().map { ChestBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return ChestBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@ChestBoatItem.boatType }
    }
}

class ShulkerBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "shulker") {

    companion object {
        val AllVersions = BoatType.values().map { ShulkerBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return ShulkerBoatEntity(DyeColor.byName(itemstack.getOrCreateTagElement("AdditionalData").getString("Color"), null), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z)
                .apply { boatType = this@ShulkerBoatItem.boatType }
                .apply { getBackingTileEntity()!!.deserializeNBT(itemstack.getOrCreateTagElement("AdditionalData").getCompound("TileEntityData")) }
    }

    override fun getContainerDisplayName(): ITextComponent {
        return TranslationTextComponent("container.shulkerBox")
    }

    override fun getName(stack: ItemStack): ITextComponent {
        val color = DyeColor.byName(stack.getOrCreateTagElement("AdditionalData").getString("Color"), null)
        if(color != null) {
            return TranslationTextComponent("item.moarboats.colored_utility_boat.name", TranslationTextComponent("item.${boatType.getBaseBoatOriginModID()}.${boatType.getShortName()}_boat"), getContainerDisplayName(), TranslationTextComponent("color.minecraft.${color.translationKey}"))
        }
        return super.getName(stack)
    }
}

class EnderChestBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "ender_chest") {

    companion object {
        val AllVersions = BoatType.values().map { EnderChestBoatItem(it) }.toTypedArray()

        operator fun get(woodType: BoatType) = AllVersions.first { it.boatType == woodType }
    }

    override fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity {
        return EnderChestBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@EnderChestBoatItem.boatType }
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
        return JukeboxBoatEntity(levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@JukeboxBoatItem.boatType }
    }

    override fun getContainerDisplayName(): ITextComponent {
        return TranslationTextComponent("block.minecraft.jukebox")
    }
}

abstract class UtilityBoatItem(val boatType: BoatType, val containerType: String): BaseBoatItem({ tab(MoarBoats.UtilityBoatTab) }) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "${boatType.getFullName()}_${containerType}_boat")
    }

    open fun getContainerDisplayName(): ITextComponent = TranslationTextComponent("container.$containerType")

    override fun getName(stack: ItemStack): ITextComponent {
        return TranslationTextComponent("item.moarboats.utility_boat.name", TranslationTextComponent("item.${boatType.getBaseBoatOriginModID()}.${boatType.getShortName()}_boat"), getContainerDisplayName())
    }
}

abstract class BaseBoatItem(propertiesModifier: Item.Properties.() -> Unit = {}): Item(Item.Properties().tab(MoarBoats.MainCreativeTab).stacksTo(1).also(propertiesModifier)) {

    override fun use(levelIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        val itemstack = playerIn.getItemInHand(handIn)
        val f1 = playerIn.xRotO + (playerIn.xRot - playerIn.xRotO) * 1.0f
        val f2 = playerIn.yRotO + (playerIn.yRot - playerIn.yRotO) * 1.0f
        val d0 = playerIn.xOld + (playerIn.x - playerIn.xOld) * 1.0
        val d1 = playerIn.yOld + (playerIn.y - playerIn.yOld) * 1.0 + playerIn.getEyeHeight().toDouble()
        val d2 = playerIn.zOld + (playerIn.z - playerIn.zOld) * 1.0
        val vec3d = Vector3d(d0, d1, d2)
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
            val list = levelIn.getEntities(playerIn, playerIn.boundingBox.expandTowards(vec3d2.x * 5.0, vec3d2.y * 5.0, vec3d2.z * 5.0).inflate(1.0))

            val flag = list.indices
                    .map { list[it] }
                    .filter { it.canBeCollidedWith() }
                    .map { it.boundingBox.inflate(it.collisionBorderSize.toDouble()) }
                    .any { it.contains(vec3d) }

            if (flag) {
                return ActionResult(ActionResultType.PASS, itemstack)
            } else if (raytraceresult.type != RayTraceResult.Type.BLOCK) {
                return ActionResult(ActionResultType.PASS, itemstack)
            } else {
                val inUsualFluid = Fluids.isUsualLiquidBlock(levelIn, raytraceresult.pos)
                val entityboat = createBoat(levelIn, raytraceresult, inUsualFluid, itemstack, playerIn)
                entityboat.yRot = playerIn.yRot

                return if (levelIn.getBlockCollisions(entityboat, entityboat.boundingBox.inflate(-0.1)).count() != 0L) {
                    ActionResult(ActionResultType.FAIL, itemstack)
                } else {
                    if (!levelIn.isClientSide) {
                        levelIn.addFreshEntity(entityboat)
                    }

                    if (!playerIn.isCreative) {
                        itemstack.shrink(1)
                    }

                    playerIn.awardStat(Stats.ITEM_USED[this])
                    ActionResult(ActionResultType.SUCCESS, itemstack)
                }
            }
        }
    }

    abstract fun createBoat(levelIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: PlayerEntity): BasicBoatEntity
}