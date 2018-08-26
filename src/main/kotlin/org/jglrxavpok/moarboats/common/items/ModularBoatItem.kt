package org.jglrxavpok.moarboats.common.items

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.extensions.Fluids

object ModularBoatItem: BaseBoatItem() {

    init {
        unlocalizedName = "modular_boat"
        registryName = ResourceLocation(MoarBoats.ModID, "modular_boat")
    }

    override fun createBoat(worldIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack): BasicBoatEntity {
        val color = EnumDyeColor.values()[itemstack.metadata % EnumDyeColor.values().size] // TODO: use something else for 1.13
        return ModularBoatEntity(worldIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z, color)
    }
}

object AnimalBoatItem: BaseBoatItem() {

    init {
        unlocalizedName = "animal_boat"
        registryName = ResourceLocation(MoarBoats.ModID, "animal_boat")
    }

    override fun createBoat(worldIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack): BasicBoatEntity {
        return AnimalBoatEntity(worldIn, raytraceresult.hitVec.x, if (inUsualFluid) raytraceresult.hitVec.y - 0.12 else raytraceresult.hitVec.y, raytraceresult.hitVec.z)
    }
}

abstract class BaseBoatItem: Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        val itemstack = playerIn.getHeldItem(handIn)
        val f = 1.0f
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
        val d3 = 5.0
        val vec3d1 = vec3d.addVector(f7.toDouble() * 5.0, f6.toDouble() * 5.0, f8.toDouble() * 5.0)
        val raytraceresult = worldIn.rayTraceBlocks(vec3d, vec3d1, true)

        if (raytraceresult == null) {
            return ActionResult(EnumActionResult.PASS, itemstack)
        } else {
            val vec3d2 = playerIn.getLook(1.0f)
            val list = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.entityBoundingBox.expand(vec3d2.x * 5.0, vec3d2.y * 5.0, vec3d2.z * 5.0).grow(1.0))

            val flag = list.indices
                    .map { list[it] }
                    .filter { it.canBeCollidedWith() }
                    .map { it.entityBoundingBox.grow(it.collisionBorderSize.toDouble()) }
                    .any { it.contains(vec3d) }

            if (flag) {
                return ActionResult(EnumActionResult.PASS, itemstack)
            } else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                return ActionResult(EnumActionResult.PASS, itemstack)
            } else {
                val block = worldIn.getBlockState(raytraceresult.blockPos)
                val inUsualFluid = Fluids.isUsualLiquidBlock(block)
                val entityboat = createBoat(worldIn, raytraceresult, inUsualFluid, itemstack)
                entityboat.rotationYaw = playerIn.rotationYaw

                return if (!worldIn.getCollisionBoxes(entityboat, entityboat.entityBoundingBox.grow(-0.1)).isEmpty()) {
                    ActionResult(EnumActionResult.FAIL, itemstack)
                } else {
                    if (!worldIn.isRemote) {
                        worldIn.spawnEntity(entityboat)
                    }

                    if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1)
                    }

                    playerIn.addStat(StatList.getObjectUseStats(this)!!)
                    ActionResult(EnumActionResult.SUCCESS, itemstack)
                }
            }
        }
    }

    abstract fun createBoat(worldIn: World, raytraceresult: RayTraceResult, inUsualFluid: Boolean, itemstack: ItemStack): BasicBoatEntity

}