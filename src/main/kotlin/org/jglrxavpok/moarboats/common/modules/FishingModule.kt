package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screens.Screen
import net.minecraft.loot.LootParameterSet
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.FishingRodItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.LootTables
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiFishingModule
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
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
    val lastLootProperty = NBTListBoatProperty("lastLoot", Tag.TAG_COMPOUND.toInt())
    val playingAnimationProperty = BooleanBoatProperty("playingAnimation")

    @OnlyIn(Dist.CLIENT)
    override fun createGui(containerID: Int, player: Player, boat: IControllable): Screen {
        return GuiFishingModule(containerID, player.inventory, this, boat)
    }

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? {
        return ContainerFishingModule(containerID, player.inventory, this, boat)
    }

    override fun getMenuType() = ContainerTypes.FishingModuleMenu.get()

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
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
        val hasRod = rodStack.item is FishingRodItem
        if(ready && hasRod && !from.worldRef.isClientSide && from.inLiquid() && !from.isEntityInLava()) { // you can go fishing
            storageModule as BoatModule

            val lureSpeed = EnchantmentHelper.getFishingSpeedBonus(rodStack)

            val randNumber = from.moduleRNG.nextInt((400 - lureSpeed*50)*9) / MoarBoatsConfig.fishing.speedMultiplier.get()
            if(randNumber <= 1f) {
                // TODO: open waters from 1.16

                val luck = EnchantmentHelper.getFishingLuckBonus(rodStack)
                // catch fish
                val builder = LootContext.Builder(from.worldRef as ServerLevel)
                builder.withLuck(luck.toFloat())
                val params = LootParameterSet.Builder().build()
                val result = from.worldRef.server!!.lootTables.get(LootTables.FISHING).getRandomItems(builder.create(params))
                val lootList = ListTag()
                result.forEach {
                    val info = CompoundTag()
                    info.putString("name", ForgeRegistries.ITEMS.getKey(it.item)!!.toString())
                    info.putInt("damage", it.damageValue)
                    lootList.add(info)
                }
                lastLootProperty[from] = lootList
                playingAnimationProperty[from] = true

                val storageInventory = from.getInventory(storageModule)
                val damageAmount = 1
                val broken = rodStack.hurt(damageAmount, from.moduleRNG, null)
                for(loot in result) {
                    val remaining = storageInventory.add(loot)
                    if(!remaining.isEmpty) {
                        from.correspondingEntity.spawnAtLocation(loot, 0f)
                    }
                }
                if(broken) {
                    breakRod(from)
                    changeRodIfPossible(from)
                    return
                } else if(rodStack.damageValue >= rodStack.maxDamage - MoarBoatsConfig.fishing.remainingUsesBeforeRemoval.get()) {
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
                lastLootProperty[from] = ListTag() // empty the loot list
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
                if(stack.item is FishingRodItem && stack.damageValue < stack.maxDamage - MoarBoatsConfig.fishing.remainingUsesBeforeRemoval.get()) {
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
        MoarBoats.network.send(PacketDistributor.ALL.noArg(), SPlaySound(from.positionX, from.positionY, from.positionZ, SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 0.8f, 0.8f + Math.random().toFloat() * 0.4f))
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
            boat.correspondingEntity.spawnAtLocation(Items.FISHING_ROD, 1) // TODO: enchantments are lost
    }
}