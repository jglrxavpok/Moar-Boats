package org.jglrxavpok.moarboats.common.items

import net.minecraft.block.BlockFence
import net.minecraft.block.FenceBlock
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.LeashKnotEntity
import net.minecraft.entity.item.LeashKnotEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

object RopeItem : MoarBoatsItem("rope") {

    enum class State {
        WAITING_NEXT,
        READY
    }

    init {
        addPropertyOverride(ResourceLocation("first_knot")) { stack, _, _ ->
            if(getState(stack) == State.WAITING_NEXT) 1f else 0f
        }
    }

    private val ropeInfo = TranslationTextComponent("item.rope.description")

    private fun setLinked(levelIn: World, stack: ItemStack, entity: BasicBoatEntity) {
        nbt(stack).putInt("linked", entity.id)
    }

    private fun getLinked(levelIn: World, stack: ItemStack): BasicBoatEntity? {
        val id = nbt(stack).getInt("linked")
        return levelIn.getEntity(id) as BasicBoatEntity?
    }

    private fun nbt(stack: ItemStack): CompoundNBT {
        if(stack.tag == null)
            stack.tag = CompoundNBT()
        return stack.tag!!
    }

    private fun resetLinked(itemstack: ItemStack) {
        nbt(itemstack).remove("linked")
    }

    fun getState(stack: ItemStack): State {
        if(nbt(stack).contains("linked"))
            return State.WAITING_NEXT
        return State.READY
    }

    override fun onItemRightClick(levelIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        resetLinked(playerIn.getItemInHand(handIn))
        return super.onItemRightClick(levelIn, playerIn, handIn)
    }

    fun onLinkUsed(itemstack: ItemStack, playerIn: PlayerEntity, handIn: Hand, levelIn: World, boatEntity: BasicBoatEntity) {
        when(getState(itemstack)) {
            State.WAITING_NEXT -> {
                val other = getLinked(levelIn, itemstack) ?: return
                val hit = boatEntity
                when {
                    other == hit -> playerIn.sendMessage(TranslationTextComponent("item.rope.notToSelf"), true)
                    hit.hasLink(BasicBoatEntity.BackLink) -> playerIn.sendMessage(TranslationTextComponent("item.rope.backOccupied"), true)
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
                    playerIn.sendMessage(TranslationTextComponent("item.rope.frontOccupied"), true)
                    resetLinked(itemstack)
                } else {
                    setLinked(levelIn, itemstack, boatEntity)
                }
            }
        }
    }

    override fun onItemUse(context: ItemUseContext): ActionResultType {
        val levelIn: World = context.level
        val pos: BlockPos = context.clickedPos
        val block = levelIn.getBlockState(pos).block
        val stack = context.itemInHand
        return when {
            block is FenceBlock && getState(stack) == State.WAITING_NEXT -> {
                if(!levelIn.isClientSide) {
                    val knot = LeashKnotEntity.getOrCreateKnot(levelIn, pos)
                    val target = getLinked(levelIn, stack) ?: return ActionResultType.PASS
                    target.linkTo(knot, BasicBoatEntity.FrontLink)
                }
                resetLinked(stack)
                ActionResultType.SUCCESS
            }
            else -> ActionResultType.PASS
        }
    }

    override fun appendHoverText(stack: ItemStack, levelIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.appendHoverText(stack, levelIn, tooltip, flagIn)
        tooltip.add(ropeInfo)
    }

    fun onEntityInteract(player: PlayerEntity, stack: ItemStack, entity: Entity): ActionResultType {
        if(getState(stack) == State.WAITING_NEXT) {
            if(entity is LeashKnotEntity) {
                val level = player.level
                if(!level.isClientSide) {
                    val target = getLinked(level, stack) ?: return ActionResultType.PASS
                    target.linkTo(entity, BasicBoatEntity.FrontLink)
                }
                resetLinked(stack)
                if(!player.isCreative)
                    stack.shrink(1)
                return ActionResultType.SUCCESS
            }
        }
        return ActionResultType.PASS
    }
}


