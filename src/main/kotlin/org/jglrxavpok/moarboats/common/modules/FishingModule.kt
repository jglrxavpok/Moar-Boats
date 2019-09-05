package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screen
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.SoundEvents
import net.minecraft.item.ItemFishingRod
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.world.WorldServer
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootTableList
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiFishingModule
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule
import org.jglrxavpok.moarboats.common.network.SPlaySound
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

    @OnlyIn(Dist.CLIENT)
    override fun createGui(player: PlayerEntity, boat: IControllable): Screen {
        return GuiFishingModule(player.inventory, this, boat)
    }

    override fun createContainer(player: PlayerEntity, boat: IControllable): ContainerBase? {
        return ContainerFishingModule(player.inventory, this, boat)
    }

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {

    }

    override fun update(from: IControllable) {
        val storageModule = from.modules.find { it.moduleSpot == Spot.Storage && it.usesInventory }
        val ready = storageModule != null
        readyProperty[from] = ready

        val inventory = from.getInventory()
        val rodStack = inventory.getItem(0)
        val hasRod = rodStack.item is ItemFishingRod
        if(ready && hasRod && !from.levelRef.isClientSide && from.inLiquid() && !from.isEntityInLava()) { // you can go fishing
            storageModule as BoatModule

            val lureSpeed = EnchantmentHelper.getFishingSpeedBonus(rodStack)

            val randNumber = from.moduleRNG.nextInt((400 - lureSpeed*50)*9) / MoarBoatsConfig.fishing.speedMultiplier.get()
            if(randNumber <= 1f) {
                val luck = EnchantmentHelper.getFishingLuckBonus(rodStack)
                // catch fish
                val builder = LootContext.Builder(from.levelRef as levelServer)
                builder.withLuck(luck.toFloat())
                val result = from.levelRef.server!!.lootTableManager.getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(from.moduleRNG, builder.build())
                val lootList = ListNBT()
                result.forEach {
                    val info = CompoundNBT()
                    info.putString("name", it.item.registryName.toString())
                    info.putInt("damage", it.damage)
                    lootList.add(info)
                }
                lastLootProperty[from] = lootList
                playingAnimationProperty[from] = true

                val storageInventory = from.getInventory(storageModule)
                val damageAmount = 1
                val broken = rodStack.attemptDamageItem(damageAmount, from.moduleRNG, null)
                for(loot in result) {
                    val remaining = storageInventory.add(loot)
                    if(!remaining.isEmpty) {
                        from.correspondingEntity.entityDropItem(loot, 0f)
                    }
                }
                if(broken) {
                    breakRod(from)
                    changeRodIfPossible(from)
                    return
                } else if(rodStack.damage >= rodStack.maxDamage - MoarBoatsConfig.fishing.remainingUsesBeforeRemoval.get()) {
                    changeRodIfPossible(from)
                    return
                }
            }
        }

        if(lastLootProperty[from].size > 0) {
            val animationTick = animationTickProperty[from]++
            if (animationTick >= MaxAnimationTicks) {
                animationTickProperty[from] = 0
                playingAnimationProperty[from] = false
                lastLootProperty[from] = ListNBT() // empty the loot list
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
            for(index in 0 until storageInventory.containerSize) {
                val stack = storageInventory.getItem(index)
                if(stack.item is ItemFishingRod && stack.damage < stack.maxDamage - MoarBoatsConfig.fishing.remainingUsesBeforeRemoval.get()) {
                    // Swap rods if possible
                    foundReplacement = true
                    storageInventory.setItem(index, inventory.getItem(0))
                    inventory.setItem(0, stack)
                    break
                }
            }
            if(!foundReplacement) {
                val rod = inventory.getItem(0)
                val remaining = storageInventory.add(rod)
                inventory.setItem(0, remaining)
            }
            inventory.syncToClient()
            storageInventory.syncToClient()
        }
    }

    private fun breakRod(from: IControllable) {
        from.getInventory().setItem(0, ItemStack.EMPTY)
        MoarBoats.network.send(PacketDistributor.ALL.noArg(), SPlaySound(from.positionX, from.positionY, from.positionZ, SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 0.8f, 0.8f + Math.random().toFloat() * 0.4f))
    }

    override fun onAddition(to: IControllable) {
        readyProperty[to] = false
    }

    override fun onInit(to: IControllable, fromItem: ItemStack?) {
        super.onInit(to, fromItem)
        if(fromItem != null) {
            to.getInventory().setItem(0, fromItem.copy())
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(Items.FISHING_ROD, 1)
    }
}