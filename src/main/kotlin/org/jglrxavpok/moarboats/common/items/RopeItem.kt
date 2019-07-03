package org.jglrxavpok.moarboats.common.items

import net.minecraft.block.BlockFence
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLeashKnot
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

object RopeItem : MoarBoatsItem("rope") {

    enum class State {
        WAITING_NEXT,
        READY
    }

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "rope")

        addPropertyOverride(ResourceLocation("firstKnot")) { stack, _, _ ->
            if(getState(stack) == State.WAITING_NEXT) 1f else 0f
        }
    }

    private val ropeInfo = TextComponentTranslation("item.rope.description")

    private fun setLinked(worldIn: World, stack: ItemStack, entity: BasicBoatEntity) {
        nbt(stack).setInt("linked", entity.entityId)
    }

    private fun getLinked(worldIn: World, stack: ItemStack): BasicBoatEntity? {
        val id = nbt(stack).getInt("linked")
        return worldIn.getEntityByID(id) as BasicBoatEntity?
    }

    private fun nbt(stack: ItemStack): NBTTagCompound {
        if(stack.tag == null)
            stack.tag = NBTTagCompound()
        return stack.tag!!
    }

    private fun resetLinked(itemstack: ItemStack) {
        nbt(itemstack).removeTag("linked")
    }

    fun getState(stack: ItemStack): State {
        if(nbt(stack).hasKey("linked"))
            return State.WAITING_NEXT
        return State.READY
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        resetLinked(playerIn.getHeldItem(handIn))
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    fun onLinkUsed(itemstack: ItemStack, playerIn: EntityPlayer, handIn: EnumHand, worldIn: World, boatEntity: BasicBoatEntity) {
        when(getState(itemstack)) {
            State.WAITING_NEXT -> {
                val other = getLinked(worldIn, itemstack) ?: return
                val hit = boatEntity
                when {
                    other == hit -> playerIn.sendStatusMessage(TextComponentTranslation("item.rope.notToSelf"), true)
                    hit.hasLink(BasicBoatEntity.BackLink) -> playerIn.sendStatusMessage(TextComponentTranslation("item.rope.backOccupied"), true)
                    else -> {
                        // first boat gets its back attached to the second boat's front
                        hit.linkTo(other, BasicBoatEntity.BackLink)
                        other.linkTo(hit, BasicBoatEntity.FrontLink)
                    }
                }
                resetLinked(itemstack)
                if(!playerIn.isCreative)
                    itemstack.shrink(1)
            }
            else -> {
                if(boatEntity.hasLink(BasicBoatEntity.Companion.FrontLink)) {
                    playerIn.sendStatusMessage(TextComponentTranslation("item.rope.frontOccupied"), true)
                    resetLinked(itemstack)
                } else {
                    setLinked(worldIn, itemstack, boatEntity)
                }
            }
        }
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val block = worldIn.getBlockState(pos).block
        val stack = player.getHeldItem(hand)
        return when {
            block is BlockFence && getState(stack) == State.WAITING_NEXT -> {
                if(!worldIn.isRemote) {
                    val knot = EntityLeashKnot.getKnotForPosition(worldIn, pos) ?: EntityLeashKnot.createKnot(worldIn, pos)
                    val target = getLinked(worldIn, stack) ?: return EnumActionResult.PASS
                    target.linkTo(knot, BasicBoatEntity.FrontLink)
                }
                resetLinked(stack)
                EnumActionResult.SUCCESS
            }
            else -> EnumActionResult.PASS
        }
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add(ropeInfo)
    }

    fun onEntityInteract(player: EntityPlayer, stack: ItemStack, entity: Entity): EnumActionResult {
        if(getState(stack) == State.WAITING_NEXT) {
            if(entity is EntityLeashKnot) {
                val world = player.world
                if(!world.isRemote) {
                    val target = getLinked(world, stack) ?: return EnumActionResult.PASS
                    target.linkTo(entity, BasicBoatEntity.FrontLink)
                }
                resetLinked(stack)
                if(!player.isCreative)
                    stack.shrink(1)
                return EnumActionResult.SUCCESS
            }
        }
        return EnumActionResult.PASS
    }
}


