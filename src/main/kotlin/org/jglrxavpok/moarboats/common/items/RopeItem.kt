package org.jglrxavpok.moarboats.common.items

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.FenceBlock
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

class RopeItem : MoarBoatsItem("rope") {

    companion object {
        private fun setLinked(levelIn: Level, stack: ItemStack, entity: BasicBoatEntity) {
            nbt(stack).putInt("linked", entity.entityID)
        }

        private fun getLinked(levelIn: Level, stack: ItemStack): BasicBoatEntity? {
            val id = nbt(stack).getInt("linked")
            return levelIn.getEntity(id) as BasicBoatEntity?
        }

        private fun nbt(stack: ItemStack): CompoundTag {
            if(stack.tag == null)
                stack.tag = CompoundTag()
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

        fun onLinkUsed(itemstack: ItemStack, playerIn: Player, handIn: InteractionHand, levelIn: Level, boatEntity: BasicBoatEntity) {
            when(getState(itemstack)) {
                State.WAITING_NEXT -> {
                    val other = getLinked(levelIn, itemstack) ?: return
                    val hit = boatEntity
                    when {
                        other == hit -> playerIn.displayClientMessage(Component.translatable("item.rope.notToSelf"), true)
                        hit.hasLink(BasicBoatEntity.BackLink) -> playerIn.displayClientMessage(Component.translatable("item.rope.backOccupied"), true)
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
                        playerIn.displayClientMessage(Component.translatable("item.rope.frontOccupied"), true)
                        resetLinked(itemstack)
                    } else {
                        setLinked(levelIn, itemstack, boatEntity)
                    }
                }
            }
        }

        fun onEntityInteract(player: Player, stack: ItemStack, entity: Entity): InteractionResult {
            if(getState(stack) == State.WAITING_NEXT) {
                if(entity is LeashFenceKnotEntity) {
                    val level = player.level
                    if(!level.isClientSide) {
                        val target = getLinked(level, stack) ?: return InteractionResult.PASS
                        target.linkTo(entity, BasicBoatEntity.FrontLink)
                    }
                    resetLinked(stack)
                    if(!player.isCreative)
                        stack.shrink(1)
                    return InteractionResult.SUCCESS
                }
            }
            return InteractionResult.PASS
        }
    }

    enum class State {
        WAITING_NEXT,
        READY
    }

    private val ropeInfo = Component.translatable("item.rope.description")

    override fun use(levelIn: Level, playerIn: Player, handIn: InteractionHand): InteractionResultHolder<ItemStack> {
        resetLinked(playerIn.getItemInHand(handIn))
        return super.use(levelIn, playerIn, handIn)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val levelIn: Level = context.level
        val pos: BlockPos = context.clickedPos
        val block = levelIn.getBlockState(pos).block
        val stack = context.itemInHand
        return when {
            block is FenceBlock && getState(stack) == State.WAITING_NEXT -> {
                if(!levelIn.isClientSide) {
                    val knot = LeashFenceKnotEntity.getOrCreateKnot(levelIn, pos)
                    val target = getLinked(levelIn, stack) ?: return InteractionResult.PASS
                    target.linkTo(knot, BasicBoatEntity.FrontLink)
                }
                resetLinked(stack)
                InteractionResult.SUCCESS
            }
            else -> InteractionResult.PASS
        }
    }

    override fun appendHoverText(stack: ItemStack, levelIn: Level?, tooltip: MutableList<Component>, flagIn: TooltipFlag) {
        super.appendHoverText(stack, levelIn, tooltip, flagIn)
        tooltip.add(ropeInfo)
    }

}


