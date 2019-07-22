package org.jglrxavpok.moarboats.common.items

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.SoundEvents
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

abstract class WaterborneItem(id: String) : MoarBoatsItem(id) {

    abstract val correspondingBlock: Block

    /**
     * Called when the equipped item is right clicked.
     */
    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        val itemstack = playerIn.getHeldItem(handIn)
        val raytraceresult = this.rayTrace(worldIn, playerIn, true)

        if (raytraceresult == null) {
            return ActionResult(EnumActionResult.PASS, itemstack)
        } else {
            if (raytraceresult.type == RayTraceResult.Type.BLOCK) {
                val blockpos = raytraceresult.blockPos

                if (!worldIn.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack)) {
                    return ActionResult(EnumActionResult.FAIL, itemstack)
                }

                val blockpos1 = blockpos.up()

                if (correspondingBlock.isValidPosition(correspondingBlock.defaultState, worldIn, blockpos1) && worldIn.isAirBlock(blockpos1)) {
                    // special case for handling block placement with water lilies
                    val blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, blockpos1)
                    worldIn.setBlockState(blockpos1, correspondingBlock.defaultState)
                    if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(playerIn, blocksnapshot, net.minecraft.util.EnumFacing.UP)) {
                        blocksnapshot.restore(true, false)
                        return ActionResult(EnumActionResult.FAIL, itemstack)
                    }

                    val facing = playerIn.adjustedHorizontalFacing.opposite
                    var iblockstate1: IBlockState = correspondingBlock.getStateForPlacement(BlockItemUseContext(worldIn, playerIn, itemstack, blockpos1, facing, raytraceresult.hitVec.x.toFloat(), raytraceresult.hitVec.y.toFloat(), raytraceresult.hitVec.z.toFloat()))
                            ?: return ActionResult(EnumActionResult.FAIL, itemstack)
                    worldIn.setBlockState(blockpos1, iblockstate1, 11)

                    iblockstate1.block.onBlockPlacedBy(worldIn, blockpos1, iblockstate1, playerIn, itemstack)

                    if (playerIn is EntityPlayerMP) {
                        CriteriaTriggers.PLACED_BLOCK.trigger(playerIn, blockpos1, itemstack)
                    }

                    if (!playerIn.isCreative) {
                        itemstack.shrink(1)
                    }

                    playerIn.addStat(StatList.ITEM_USED[this])
                    worldIn.playSound(playerIn, blockpos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    return ActionResult(EnumActionResult.SUCCESS, itemstack)
                }
            }

            return ActionResult(EnumActionResult.FAIL, itemstack)
        }
    }
}