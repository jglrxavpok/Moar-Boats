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
    override fun onItemRightClick(levelIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        val itemstack = playerIn.getHeldItem(handIn)
        val rawResult = rayTrace(levelIn, playerIn, RayTraceContext.FluidMode.ANY)

        if (rawResult == null) {
            return ActionResult(ActionResultType.PASS, itemstack)
        } else {
            if (rawResult.type == RayTraceResult.Type.BLOCK) {
                val raytraceresult = rawResult as BlockRayTraceResult
                val blockpos = raytraceresult.pos

                if (!levelIn.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.offset(raytraceresult.face), raytraceresult.face, itemstack)) {
                    return ActionResult(ActionResultType.FAIL, itemstack)
                }

                val blockpos1 = blockpos.up()

                if (correspondingBlock.isValidPosition(correspondingBlock.defaultState, levelIn, blockpos1) && levelIn.isAirBlock(blockpos1)) {
                    // special case for handling block placement with water lilies
                    val blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(levelIn.registryKey, levelIn, blockpos1)
                    levelIn.setBlockState(blockpos1, correspondingBlock.defaultState)
                    if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(playerIn, blocksnapshot, net.minecraft.util.Direction.UP)) {
                        blocksnapshot.restore(true, false)
                        return ActionResult(ActionResultType.FAIL, itemstack)
                    }

                    val facing = playerIn.adjustedHorizontalFacing.opposite
                    var iblockstate1: BlockState = correspondingBlock.getStateForPlacement(BlockItemUseContext(ItemUseContext(playerIn, handIn, raytraceresult)))
                            ?: return ActionResult(ActionResultType.FAIL, itemstack)
                    levelIn.setBlockState(blockpos1, iblockstate1, 11)

                    iblockstate1.block.onBlockPlacedBy(levelIn, blockpos1, iblockstate1, playerIn, itemstack)

                    if (playerIn is ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger(playerIn, blockpos1, itemstack)
                    }

                    if (!playerIn.isCreative) {
                        itemstack.shrink(1)
                    }

                    playerIn.addStat(Stats.ITEM_USED[this])
                    levelIn.playSound(playerIn, blockpos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    return ActionResult(ActionResultType.SUCCESS, itemstack)
                }
            }

            return ActionResult(ActionResultType.FAIL, itemstack)
        }
    }
}