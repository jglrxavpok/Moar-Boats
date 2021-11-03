package org.jglrxavpok.moarboats.common.items

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.SoundEvents
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.stats.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.RayTraceContext
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

abstract class WaterborneItem(id: String) : MoarBoatsItem(id) {

    abstract val correspondingBlock: Block

    /**
     * Called when the equipped item is right clicked.
     */
    override fun use(levelIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        val itemstack = playerIn.getItemInHand(handIn)

        val rawResult = getPlayerPOVHitResult(levelIn, playerIn, RayTraceContext.FluidMode.ANY)

        if (rawResult == null) {
            return ActionResult(ActionResultType.PASS, itemstack)
        } else {
            if (rawResult.type == RayTraceResult.Type.BLOCK) {
                val raytraceresult = rawResult as BlockRayTraceResult
                val blockpos = raytraceresult.blockPos

                if (!levelIn.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.relative(raytraceresult.direction), raytraceresult.direction, itemstack)) {
                    return ActionResult(ActionResultType.FAIL, itemstack)
                }

                val blockpos1 = blockpos.above()

                if (correspondingBlock.defaultBlockState().canSurvive(levelIn, blockpos1) && levelIn.isEmptyBlock(blockpos1)) {
                    // special case for handling block placement with water lilies
                    val blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(levelIn.dimension(), levelIn, blockpos1)
                    levelIn.setBlockAndUpdate(blockpos1, correspondingBlock.defaultBlockState())
                    if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(playerIn, blocksnapshot, net.minecraft.util.Direction.UP)) {
                        blocksnapshot.restore(true, false)
                        return ActionResult(ActionResultType.FAIL, itemstack)
                    }

                    var iblockstate1: BlockState = correspondingBlock.getStateForPlacement(BlockItemUseContext(ItemUseContext(playerIn, handIn, raytraceresult)))
                            ?: return ActionResult(ActionResultType.FAIL, itemstack)
                    levelIn.setBlock(blockpos1, iblockstate1, 11)

                    iblockstate1.block.setPlacedBy(levelIn, blockpos1, iblockstate1, playerIn, itemstack)

                    if (playerIn is ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger(playerIn, blockpos1, itemstack)
                    }

                    if (!playerIn.isCreative) {
                        itemstack.shrink(1)
                    }

                    playerIn.awardStat(Stats.ITEM_USED[this])
                    levelIn.playSound(playerIn, blockpos, SoundEvents.LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    return ActionResult(ActionResultType.SUCCESS, itemstack)
                }
            }

            return ActionResult(ActionResultType.FAIL, itemstack)
        }
    }
}