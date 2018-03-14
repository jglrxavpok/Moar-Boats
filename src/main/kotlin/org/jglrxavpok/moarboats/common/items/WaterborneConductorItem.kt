package org.jglrxavpok.moarboats.common.items

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.util.*
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.blocks.BlockUnpoweredWaterboneConductor

object WaterborneConductorItem : Item() {

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "waterborne_conductor"
        registryName = ResourceLocation(MoarBoats.ModID, "waterborne_conductor")
        maxStackSize = 64
    }

    /**
     * Called when the equipped item is right clicked.
     */
    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        val itemstack = playerIn.getHeldItem(handIn)
        val raytraceresult = this.rayTrace(worldIn, playerIn, true)

        if (raytraceresult == null) {
            return ActionResult(EnumActionResult.PASS, itemstack)
        } else {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                val blockpos = raytraceresult.blockPos

                if (!worldIn.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack)) {
                    return ActionResult(EnumActionResult.FAIL, itemstack)
                }

                val blockpos1 = blockpos.up()

                if (BlockUnpoweredWaterboneConductor.canPlaceBlockAt(worldIn, blockpos1) && worldIn.isAirBlock(blockpos1)) {
                    // special case for handling block placement with water lilies
                    val blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, blockpos1)
                    worldIn.setBlockState(blockpos1, BlockUnpoweredWaterboneConductor.defaultState)
                    if (net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(playerIn, blocksnapshot, net.minecraft.util.EnumFacing.UP, handIn).isCanceled) {
                        blocksnapshot.restore(true, false)
                        return ActionResult(EnumActionResult.FAIL, itemstack)
                    }

                    val facing = playerIn.adjustedHorizontalFacing.opposite
                    var iblockstate1 = BlockUnpoweredWaterboneConductor.getStateForPlacement(worldIn, blockpos1, facing, raytraceresult.hitVec.x.toFloat(), raytraceresult.hitVec.y.toFloat(), raytraceresult.hitVec.z.toFloat(), 0, playerIn, handIn)
                    worldIn.setBlockState(blockpos1, iblockstate1, 11)

                    iblockstate1.block.onBlockPlacedBy(worldIn, blockpos1, iblockstate1, playerIn, itemstack)

                    if (playerIn is EntityPlayerMP) {
                        CriteriaTriggers.PLACED_BLOCK.trigger(playerIn, blockpos1, itemstack)
                    }

                    if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1)
                    }

                    playerIn.addStat(StatList.getObjectUseStats(this)!!)
                    worldIn.playSound(playerIn, blockpos, SoundEvents.BLOCK_WATERLILY_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    return ActionResult(EnumActionResult.SUCCESS, itemstack)
                }
            }

            return ActionResult(EnumActionResult.FAIL, itemstack)
        }
    }
}