package org.jglrxavpok.moarboats.common.items

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext

abstract class WaterborneItem(id: String) : MoarBoatsItem(id) {

    abstract val correspondingBlock: Block

    /**
     * Called when the equipped item is right clicked.
     */
    override fun use(levelIn: Level, playerIn: Player, handIn: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemstack = playerIn.getItemInHand(handIn)

        val rawResult = getPlayerPOVHitResult(levelIn, playerIn, ClipContext.Fluid.ANY)

        if (rawResult == null) {
            return InteractionResultHolder(InteractionResult.PASS, itemstack)
        } else {
            if (rawResult.type == HitResult.Type.BLOCK) {
                val raytraceresult = rawResult as BlockHitResult
                val blockpos = raytraceresult.blockPos

                val selectionContext = CollisionContext.of(playerIn);
                val state = levelIn.getBlockState(blockpos)
                if (!levelIn.isUnobstructed(state, blockpos, selectionContext)/* || !playerIn.canPlayerEdit(blockpos.relative(raytraceresult.direction), raytraceresult.direction, itemstack)*/) {
                    return InteractionResultHolder(InteractionResult.FAIL, itemstack)
                }

                val blockpos1 = blockpos.above()

                if (correspondingBlock.defaultBlockState().canSurvive(levelIn, blockpos1) && levelIn.isEmptyBlock(blockpos1)) {
                    // special case for handling block placement with water lilies
                    val blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(levelIn.dimension(), levelIn, blockpos1)
                    levelIn.setBlockAndUpdate(blockpos1, correspondingBlock.defaultBlockState())
                    if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(playerIn, blocksnapshot, Direction.UP)) {
                        blocksnapshot.restore(true, false)
                        return InteractionResultHolder(InteractionResult.FAIL, itemstack)
                    }

                    var iblockstate1: BlockState = correspondingBlock.getStateForPlacement(BlockPlaceContext(
                        UseOnContext(playerIn, handIn, raytraceresult)
                    ))
                            ?: return InteractionResultHolder(InteractionResult.FAIL, itemstack)
                    levelIn.setBlock(blockpos1, iblockstate1, 11)

                    iblockstate1.block.setPlacedBy(levelIn, blockpos1, iblockstate1, playerIn, itemstack)

                    if (playerIn is ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger(playerIn, blockpos1, itemstack)
                    }

                    if (!playerIn.isCreative) {
                        itemstack.shrink(1)
                    }

                    playerIn.awardStat(Stats.ITEM_USED[this])
                    levelIn.playSound(playerIn, blockpos, SoundEvents.LILY_PAD_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f)
                    return InteractionResultHolder(InteractionResult.SUCCESS, itemstack)
                }
            }

            return InteractionResultHolder(InteractionResult.FAIL, itemstack)
        }
    }
}