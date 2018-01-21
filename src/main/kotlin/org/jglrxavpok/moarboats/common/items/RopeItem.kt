package org.jglrxavpok.moarboats.common.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

object RopeItem : Item() {

    enum class State {
        WAITING_NEXT,
        READY
    }

    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "rope"
        registryName = ResourceLocation(MoarBoats.ModID, "rope")
        maxStackSize = 1

        addPropertyOverride(ResourceLocation("firstKnot")) { stack, _, _ ->
            if(getState(stack) == State.WAITING_NEXT) 1f else 0f
        }
    }

    private fun setLinked(worldIn: World, stack: ItemStack, entity: BasicBoatEntity) {
        nbt(stack).setInteger("linked", entity.entityId)
    }

    private fun getLinked(worldIn: World, stack: ItemStack): BasicBoatEntity? {
        val id = nbt(stack).getInteger("linked")
        return worldIn.getEntityByID(id) as BasicBoatEntity?
    }

    private fun nbt(stack: ItemStack): NBTTagCompound {
        if(stack.tagCompound == null)
            stack.tagCompound = NBTTagCompound()
        return stack.tagCompound!!
    }

    private fun resetLinked(itemstack: ItemStack) {
        nbt(itemstack).removeTag("linked")
    }

    private fun getState(stack: ItemStack): State {
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
                    hit.hasLink(BasicBoatEntity.Companion.BackLink) -> playerIn.sendStatusMessage(TextComponentTranslation("item.rope.backOccupied"), true)
                    else -> {
                        // first boat gets its back attached to the second boat's front
                        hit.linkTo(other, BasicBoatEntity.Companion.BackLink)
                        other.linkTo(hit, BasicBoatEntity.Companion.FrontLink)
                    }
                }
                resetLinked(itemstack) // TODO: consume
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
}


