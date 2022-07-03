package org.jglrxavpok.moarboats.common.items

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
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
import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.common.Cleats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

class RopeItem : MoarBoatsItem("rope") {

    companion object {
        private fun setLinked(levelIn: Level, stack: ItemStack, entity: BasicBoatEntity, cleat: Cleat) {
            nbt(stack).putInt("linked", entity.entityID)
            nbt(stack).putString("cleat", Cleats.Registry.get().getKey(cleat)?.toString() ?: error("Cleat not registered"))
        }

        private fun getLinked(levelIn: Level, stack: ItemStack): BasicBoatEntity? {
            val id = nbt(stack).getInt("linked")
            return levelIn.getEntity(id) as BasicBoatEntity?
        }

        private fun getLinkedCleat(levelIn: Level, stack: ItemStack): Cleat? {
            val data = nbt(stack)
            if(data.contains("cleat"))
                return Cleats.Registry.get().getValue(ResourceLocation(data.getString("cleat")))
            return null
        }

        private fun nbt(stack: ItemStack): CompoundTag {
            if(stack.tag == null)
                stack.tag = CompoundTag()
            return stack.tag!!
        }

        private fun resetLinked(itemstack: ItemStack) {
            nbt(itemstack).remove("linked")
            nbt(itemstack).remove("cleat")
        }

        fun getState(stack: ItemStack): State {
            if(nbt(stack).contains("linked"))
                return State.WAITING_NEXT
            return State.READY
        }

        fun onLinkUsed(itemstack: ItemStack, playerIn: Player, handIn: InteractionHand, levelIn: Level, boatEntity: BasicBoatEntity, clickedCleat: Cleat) {
            when(getState(itemstack)) {
                State.WAITING_NEXT -> {
                    val other = getLinked(levelIn, itemstack) ?: return
                    val hit = boatEntity
                    val linkedCleat = getLinkedCleat(levelIn, itemstack) ?: error("anchor type == null but linked != null")
                    when {
                        !linkedCleat.supportsConnection(clickedCleat) -> playerIn.displayClientMessage(Component.translatable("item.rope.incompatible_anchor_types"), true)
                        other == hit -> playerIn.displayClientMessage(Component.translatable("item.rope.notToSelf"), true)
                        hit.hasLink(clickedCleat) -> playerIn.displayClientMessage(Component.translatable("item.rope.backOccupied"), true)
                        else -> {
                            hit.linkTo(other, clickedCleat, linkedCleat)
                            other.linkTo(hit, linkedCleat, clickedCleat)
                        }
                    }
                    resetLinked(itemstack)
                    if(!playerIn.isCreative)
                        itemstack.shrink(1)
                }
                else -> {
                    if(boatEntity.hasLink(clickedCleat)) {
                        playerIn.displayClientMessage(Component.translatable("item.rope.frontOccupied"), true)
                        resetLinked(itemstack)
                    } else {
                        setLinked(levelIn, itemstack, boatEntity, clickedCleat)
                    }
                }
            }
            playerIn.setItemInHand(handIn, itemstack)
        }

        fun onEntityInteract(player: Player, stack: ItemStack, entity: Entity): InteractionResult {
            // TODO: fix fence knots
            if(getState(stack) == State.WAITING_NEXT) {
                if(entity is LeashFenceKnotEntity) {
                    val level = player.level
                    if(!level.isClientSide) {
                        val target = getLinked(level, stack) ?: return InteractionResult.PASS
                        TODO("target.linkTo(entity, BasicBoatEntity.FrontLink)")
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
       // resetLinked(playerIn.getItemInHand(handIn))
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
                    // TODO: target.linkTo(knot, BasicBoatEntity.FrontLink)
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


