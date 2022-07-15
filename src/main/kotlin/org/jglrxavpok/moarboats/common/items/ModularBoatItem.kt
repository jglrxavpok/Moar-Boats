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
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.utilityboats.*
import org.jglrxavpok.moarboats.extensions.Fluids
import java.util.*

class ModularBoatItem(val dyeColor: DyeColor): BaseBoatItem() {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        val color = dyeColor
        return ModularBoatEntity(
                EntityEntries.ModularBoat.get(),
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

class AnimalBoatItem: BaseBoatItem() {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return AnimalBoatEntity(EntityEntries.AnimalBoat.get(), levelIn)
    }
}

class FurnaceBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "furnace") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return FurnaceBoatEntity(EntityEntries.FurnaceBoat.get(), levelIn).apply { boatType = this@FurnaceBoatItem.boatType }
    }
}

class SmokerBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "smoker") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return SmokerBoatEntity(EntityEntries.SmokerBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@SmokerBoatItem.boatType }
    }
}

class BlastFurnaceBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "blast_furnace") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return BlastFurnaceBoatEntity(EntityEntries.BlastFurnaceBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@BlastFurnaceBoatItem.boatType }
    }
}

class CraftingTableBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "crafting_table") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return CraftingTableBoatEntity(EntityEntries.CraftingTableBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@CraftingTableBoatItem.boatType }
    }

    override fun getContainerDisplayName(): Component {
        return Component.translatable("container.crafting")
    }
}

class GrindstoneBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "grindstone") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return GrindstoneBoatEntity(EntityEntries.GrindstoneBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@GrindstoneBoatItem.boatType }
    }

    override fun getName(stack: ItemStack): Component {
        return Component.translatable("item.moarboats.utility_boat.name", Component.translatable("item.${boatType.getBaseBoatOriginModID()}.${boatType.getShortName()}_boat"), Component.translatable("block.minecraft.grindstone"))
    }
}

class CartographyTableBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "cartography_table") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return CartographyTableBoatEntity(EntityEntries.CartographyTableBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@CartographyTableBoatItem.boatType }
    }
}

class LoomBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "loom") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return LoomBoatEntity(EntityEntries.LoomBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@LoomBoatItem.boatType }
    }
}

class StonecutterBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "stonecutter") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return StonecutterBoatEntity(EntityEntries.StonecutterBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@StonecutterBoatItem.boatType }
    }
}

class ChestBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "chest") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return ChestBoatEntity(EntityEntries.ChestBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@ChestBoatItem.boatType }
    }
}

class ShulkerBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "shulker") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return ShulkerBoatEntity(EntityEntries.ShulkerBoat.get(), DyeColor.byName(itemstack.getOrCreateTagElement("AdditionalData").getString("Color"), null), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z)
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

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return EnderChestBoatEntity(EntityEntries.EnderChestBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@EnderChestBoatItem.boatType }
    }

    override fun getContainerDisplayName(): Component {
        return Component.translatable("block.minecraft.ender_chest")
    }
}

class JukeboxBoatItem(woodType: BoatType): UtilityBoatItem(woodType, "jukebox") {

    override fun createBoat(levelIn: Level, raytraceresult: HitResult, inUsualFluid: Boolean, itemstack: ItemStack, playerIn: Player): BasicBoatEntity {
        return JukeboxBoatEntity(EntityEntries.JukeboxBoat.get(), levelIn, raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z).apply { boatType = this@JukeboxBoatItem.boatType }
    }

    override fun getContainerDisplayName(): Component {
        return Component.translatable("block.minecraft.jukebox")
    }
}

abstract class UtilityBoatItem(val boatType: BoatType, val containerType: String): BaseBoatItem({ tab(MoarBoats.UtilityBoatTab) }) {

    open fun getContainerDisplayName(): Component = Component.translatable("container.$containerType")

    override fun getName(stack: ItemStack): Component {
        return Component.translatable("item.moarboats.utility_boat.name", Component.translatable("item.${boatType.getBaseBoatOriginModID()}.${boatType.getShortName()}_boat"), getContainerDisplayName())
    }
}

abstract class BaseBoatItem(propertiesModifier: Item.Properties.() -> Unit = {}): Item(Item.Properties().tab(MoarBoats.MainCreativeTab).stacksTo(1).also(propertiesModifier)) {

    override fun use(levelIn: Level, playerIn: Player, handIn: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemstack = playerIn.getItemInHand(handIn)
        if (levelIn.isClientSide) {
            return InteractionResultHolder.consume(itemstack)
        }

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
                entityboat.setPos(raytraceresult.location.x, if (inUsualFluid) raytraceresult.location.y - 0.12 else raytraceresult.location.y, raytraceresult.location.z)
                entityboat.yRot = playerIn.yRot

                return if (levelIn.getBlockCollisions(entityboat, entityboat.boundingBox.inflate(-0.1)).count() != 0) {
                    InteractionResultHolder(InteractionResult.FAIL, itemstack)
                } else {
                    levelIn.addFreshEntity(entityboat)

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