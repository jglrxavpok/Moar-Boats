package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.Container
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemFishingRod
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldServer
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootTableList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiChestModule
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiFishingModule
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule

object FishingModule : BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "fishing")

    override val usesInventory = true
    override val moduleSpot = Spot.Misc
    override val hopperPriority = 20

    const val MaxAnimationTicks = 10
    // State names
    const val READY = "ready"
    const val ANIMATION_TICK = "animationTick"
    const val LAST_LOOT = "lastLoot"
    const val PLAYING_ANIMATION = "playingAnimation"

    @SideOnly(Side.CLIENT)
    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiFishingModule(player.inventory, this, boat)
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container? {
        return ContainerFishingModule(player.inventory, this, boat)
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {

    }

    override fun update(from: IControllable) {
        val storageModule = from.modules.find { it.moduleSpot == Spot.Storage }
        val ready = storageModule != null
        val state = from.getState()
        state.setBoolean(READY, ready)

        val inventory = from.getInventory()
        val rodStack = inventory.getStackInSlot(0)
        val hasRod = rodStack.item is ItemFishingRod
        if(ready && hasRod && !from.worldRef.isRemote) { // you can go fishing

            val lureSpeed = EnchantmentHelper.getFishingSpeedBonus(rodStack)

            val randNumber = from.moduleRNG.nextInt(400 - lureSpeed*50)
            if(randNumber <= 1) {
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
                state.setTag(LAST_LOOT, lootList)
                state.setBoolean(PLAYING_ANIMATION, true)

                println("caught something!")

                if(storageModule != null) {
                    val storageInventory = from.getInventory(storageModule)
                    for(loot in result) {
                        val remaining = storageInventory.add(loot)
                        if(!remaining.isEmpty) {
                            from.correspondingEntity.entityDropItem(loot, 0f)
                        }
                    }
                }
            }
        }

        if(state.getTagList(LAST_LOOT, Constants.NBT.TAG_COMPOUND).tagCount() > 0) {
            var animationTick = state.getInteger(ANIMATION_TICK)
            animationTick++
            if (animationTick >= MaxAnimationTicks) {
                animationTick = 0
                state.setBoolean(PLAYING_ANIMATION, false)
                state.setTag(LAST_LOOT, NBTTagList()) // empty the loot list
            }
            state.setInteger(ANIMATION_TICK, animationTick)
        } else {
            state.setInteger(ANIMATION_TICK, 0)
            state.setBoolean(PLAYING_ANIMATION, false)
        }
        from.saveState()
    }

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        state.setBoolean(READY, false)
        to.saveState()
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