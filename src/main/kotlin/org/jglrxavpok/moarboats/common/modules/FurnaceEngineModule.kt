package org.jglrxavpok.moarboats.common.modules

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.common.ForgeHooks
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerFurnaceEngine
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.state.IntBoatProperty
import org.jglrxavpok.moarboats.extensions.toRadians

object FurnaceEngineModule : BaseEngineModule() {
    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? {
        return ContainerFurnaceEngine(menuType as MenuType<ContainerFurnaceEngine>, containerID, player.inventory, this, boat)
    }


    override val id = ResourceLocation("moarboats:furnace_engine")
    override val usesInventory = true

    // State names
    val fuelTotalTimeProperty = IntBoatProperty("fuelTotalTime")
    val fuelTimeProperty = IntBoatProperty("fuelTime")

    override fun remainingTimeInTicks(from: IControllable): Float {
        return (fuelTotalTimeProperty[from] - fuelTimeProperty[from]).toFloat()
    }

    override fun remainingTimeInPercent(from: IControllable): Float {
        val currentFuel = fuelTimeProperty[from]
        val totalFuel = fuelTotalTimeProperty[from]
        return if(totalFuel == 0) 0f else 1f - currentFuel / totalFuel.toFloat()
    }

    override fun estimatedTotalTicks(boat: IControllable): Float {
        val inv = boat.getInventory()
        val diff = remainingTimeInTicks(boat)
        val currentStack = inv.getItem(0)
        return diff + currentStack.count * getFuelTime(currentStack)
    }

    override fun onAddition(to: IControllable) {
        super.onAddition(to)
        fuelTimeProperty[to] = 0
        fuelTotalTimeProperty[to] = 0
    }

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun hasFuel(from: IControllable): Boolean {
        val fuelTime = fuelTimeProperty[from]
        val fuelTotalTime = fuelTotalTimeProperty[from]
        return fuelTime < fuelTotalTime
    }

    override fun updateFuelState(boat: IControllable, state: CompoundTag, inv: Container) {
        val fuelTime = fuelTimeProperty[boat]
        val fuelTotalTime = fuelTotalTimeProperty[boat]
        if(fuelTime < fuelTotalTime) {
            fuelTimeProperty[boat]++
        } else {
            var stack = inv.getItem(0)
            if(stack.isEmpty) {
                tryToFindFuel(boat)
                stack = inv.getItem(0)
            }
            val fuelItem = stack.item
            val itemFuelTime = getFuelTime(stack)
            if (itemFuelTime > 0 && !isStationary(boat)) { // don't consume a new item if you are not moving
                if(fuelItem == Items.LAVA_BUCKET)
                    inv.setItem(0, ItemStack(Items.BUCKET))
                else
                    inv.removeItem(0, 1)
                fuelTimeProperty[boat] = 0
                fuelTotalTimeProperty[boat] = itemFuelTime
            }
        }

        if(hasFuel(boat) && rng.nextInt(4) == 0) {
            val cos = Mth.cos((boat.yaw + 90f).toRadians())
            val sin = Mth.sin((boat.yaw + 90f).toRadians())
            val dist = 0.5
            boat.worldRef.addParticle(ParticleTypes.LARGE_SMOKE, boat.positionX + dist * cos, boat.positionY + 0.8, boat.positionZ + dist * sin, 0.0, 0.0, 0.0)
        }
    }

    private fun tryToFindFuel(boat: IControllable) {
        val storageModule = boat.modules.find { it.moduleSpot == Spot.Storage && it.usesInventory }
        if(storageModule != null) {
            val inventory = boat.getInventory()
            val storageInventory = boat.getInventory(storageModule)
            for(index in 0 until storageInventory.containerSize) {
                val stack = storageInventory.getItem(index)
                if(isItemFuel(stack)) {
                    storageInventory.setItem(index, ItemStack.EMPTY)
                    inventory.setItem(0, stack)
                    break
                }
            }
            inventory.syncToClient()
            storageInventory.syncToClient()
        }
    }

    override fun getFuelTime(fuelItem: ItemStack): Int {
        return when(fuelItem.item) {
            Blocks.TORCH.asItem() -> 1*SECONDS_TO_TICKS
            Items.COAL -> 60*3*SECONDS_TO_TICKS
            Items.LAVA_BUCKET -> 60*15*SECONDS_TO_TICKS
            Blocks.MAGMA_BLOCK.asItem() -> 60*30*SECONDS_TO_TICKS
            Blocks.COAL_BLOCK.asItem() -> 30*30*SECONDS_TO_TICKS
            else -> (getBurnTime(fuelItem)*.9).toInt() // scale time to make lava bucket burn time the same duration as the one above
        }
    }

    /**
     * From AbstractFurnaceTileEntity
     */
    fun getBurnTime(stack: ItemStack): Int {
        return if (stack.isEmpty) {
            0
        } else {
            ForgeHooks.getBurnTime(stack, RecipeType.SMELTING)
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(Blocks.FURNACE.asItem(), 1)
    }
}