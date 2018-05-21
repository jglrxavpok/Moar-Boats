package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemFishingRod
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.world.WorldServer
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootTableList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiFishingModule
import org.jglrxavpok.moarboats.common.MBConfig
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule
import org.jglrxavpok.moarboats.common.network.S6PlaySound
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import org.jglrxavpok.moarboats.common.state.NBTListBoatProperty

object FishingModule : BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "fishing")

    override val usesInventory = true
    override val moduleSpot = Spot.Misc
    override val hopperPriority = 0

    const val MaxAnimationTicks = 10
    // State properties
    val readyProperty = BooleanBoatProperty("ready")
    val animationTickProperty = IntBoatProperty("animationTick")
    val lastLootProperty = NBTListBoatProperty("lastLoot", Constants.NBT.TAG_COMPOUND)
    val playingAnimationProperty = BooleanBoatProperty("playingAnimation")

    @SideOnly(Side.CLIENT)
    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiFishingModule(player.inventory, this, boat)
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase? {
        return ContainerFishingModule(player.inventory, this, boat)
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {

    }

    override fun update(from: IControllable) {
        val storageModule = from.modules.find { it.moduleSpot == Spot.Storage && it.usesInventory }
        val ready = storageModule != null
        readyProperty[from] = ready

        val inventory = from.getInventory()
        val rodStack = inventory.getStackInSlot(0)
        val hasRod = rodStack.item is ItemFishingRod
        if(ready && hasRod && !from.worldRef.isRemote && from.inLiquid() && !from.isEntityInLava()) { // you can go fishing
            storageModule as BoatModule

            val lureSpeed = EnchantmentHelper.getFishingSpeedBonus(rodStack)

            val randNumber = from.moduleRNG.nextInt((400 - lureSpeed*50)*3) / MBConfig.fishingSpeedMultiplier
            if(randNumber <= 1f) {
                val luck = EnchantmentHelper.getFishingLuckBonus(rodStack)
                // catch fish
                val builder = LootContext.Builder(from.worldRef as WorldServer)
                builder.withLuck(luck.toFloat())
                val result = from.worldRef.lootTableManager.getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(from.moduleRNG, builder.build())
                val lootList = NBTTagList()
                result.forEach {
                    val info = NBTTagCompound()
                    info.setString("name", it.item.registryName.toString())
                    info.setInteger("damage", it.itemDamage)
                    lootList.appendTag(info)
                }
                lastLootProperty[from] = lootList
                playingAnimationProperty[from] = true

                val storageInventory = from.getInventory(storageModule)
                val damageAmount = 1
                val broken = rodStack.attemptDamageItem(damageAmount, from.moduleRNG, null)
                if(broken) {
                    breakRod(from)
                    changeRodIfPossible(from)
                    return
                } else if(rodStack.itemDamage >= rodStack.maxDamage - MBConfig.fishingRemainingUsesBeforeBreak) {
                    changeRodIfPossible(from)
                    return
                }
                for(loot in result) {
                    val remaining = storageInventory.add(loot)
                    if(!remaining.isEmpty) {
                        from.correspondingEntity.entityDropItem(loot, 0f)
                    }
                }
            }
        }

        if(lastLootProperty[from].tagCount() > 0) {
            val animationTick = animationTickProperty[from]++
            if (animationTick >= MaxAnimationTicks) {
                animationTickProperty[from] = 0
                playingAnimationProperty[from] = false
                lastLootProperty[from] = NBTTagList() // empty the loot list
            }
        } else {
            animationTickProperty[from] = 0
            playingAnimationProperty[from] = false
        }
    }

    private fun changeRodIfPossible(from: IControllable) {
        val storageModule = from.modules.find { it.moduleSpot == Spot.Storage && it.usesInventory }
        if(storageModule != null) {
            val inventory = from.getInventory()
            val storageInventory = from.getInventory(storageModule)
            var foundReplacement = false
            for(index in 0 until storageInventory.sizeInventory) {
                val stack = storageInventory.getStackInSlot(index)
                if(stack.item is ItemFishingRod && stack.itemDamage < stack.maxDamage - MBConfig.fishingRemainingUsesBeforeBreak) {
                    // Swap rods if possible
                    foundReplacement = true
                    storageInventory.setInventorySlotContents(index, inventory.getStackInSlot(0))
                    inventory.setInventorySlotContents(0, stack)
                    break
                }
            }
            if(!foundReplacement) {
                val rod = inventory.getStackInSlot(0)
                val remaining = storageInventory.add(rod)
                inventory.setInventorySlotContents(0, remaining)
            }
            inventory.syncToClient()
            storageInventory.syncToClient()
        }
    }

    private fun breakRod(from: IControllable) {
        from.getInventory().setInventorySlotContents(0, ItemStack.EMPTY)
        MoarBoats.network.sendToAll(S6PlaySound(from.positionX, from.positionY, from.positionZ, SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 0.8f, 0.8f + Math.random().toFloat() * 0.4f))
    }

    override fun onAddition(to: IControllable) {
        readyProperty[to] = false
    }

    override fun onInit(to: IControllable, fromItem: ItemStack?) {
        super.onInit(to, fromItem)
        if(fromItem != null) {
            to.getInventory().setInventorySlotContents(0, fromItem.copy())
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(Items.FISHING_ROD, 1)
    }
}