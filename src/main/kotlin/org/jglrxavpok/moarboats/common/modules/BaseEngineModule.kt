package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.Block
import net.minecraft.block.BlockHopper
import net.minecraft.block.material.Material
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.particle.ParticleBreaking
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Particles
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.particles.BlockParticleData
import net.minecraft.particles.IParticleData
import net.minecraft.particles.ItemParticleData
import net.minecraft.tileentity.TileEntityHopper
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiEngineModule
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import org.jglrxavpok.moarboats.common.state.FloatBoatProperty
import org.jglrxavpok.moarboats.extensions.toRadians

abstract class BaseEngineModule: BoatModule() {

    val stationaryProperty = BooleanBoatProperty("stationary")
    val lockedByRedstoneProperty = BooleanBoatProperty("redstoneLocked")
    val speedProperty = FloatBoatProperty("speedSetting")
    override val moduleSpot = Spot.Engine
    override val hopperPriority = 0

    abstract fun hasFuel(from: IControllable): Boolean
    abstract fun getFuelTime(fuelItem: ItemStack): Int
    protected abstract fun updateFuelState(boat: IControllable, state: NBTTagCompound, inv: IInventory)
    abstract fun remainingTimeInTicks(from: IControllable): Float
    abstract fun remainingTimeInPercent(from: IControllable): Float
    abstract fun estimatedTotalTicks(boat: IControllable): Float

    fun isStationary(from: IControllable) = stationaryProperty[from] || from.blockedReason != NoBlockReason

    override fun controlBoat(from: IControllable) {
        if(hasFuel(from) && !isStationary(from) && from.inLiquid()) {
            val speed = if(from.isSpeedImposed()) from.imposedSpeed else speedProperty[from]
            from.accelerate(speed+1f)
        }

        if(lockedByRedstoneProperty[from]) {
            if( ! from.worldRef.isRemote) {
                // special case for full hoppers (see #81)
                val storage = from.modules.firstOrNull { it.moduleSpot == Spot.Storage }
                if(storage != null && storage.usesInventory) {
                    val hopperPos = from.correspondingEntity.position.up()
                    val blockState = from.worldRef.getBlockState(hopperPos)
                    if(blockState.block is BlockHopper) {
                        val te = from.worldRef.getTileEntity(hopperPos) as TileEntityHopper
                        val storageInventory = from.getInventory(storage)
                        if( ! storageInventory.canAddAnyFrom(te)) {
                            return // bypass lock
                        }
                    }
                }
            }
            from.blockMovement(BlockedByRedstone)
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiEngineModule(player.inventory, this, boat, createContainer(player, boat)!!)
    }

    override fun onAddition(to: IControllable) {
        stationaryProperty[to] = true
        lockedByRedstoneProperty[to] = false
        speedProperty[to] = 0f
    }

    override fun update(from: IControllable) {
        val state = from.getState()
        if(usesInventory) {
            val inv = from.getInventory()
            updateFuelState(from, state, inv)
        }
        val world = from.worldRef
        lockedByRedstoneProperty[from] = world.isBlockPowered(from.correspondingEntity.position)
        from.saveState()
        if(hasFuel(from) && !isStationary(from)) {
            val count = ((Math.random() * 5) + 3).toInt()
            val posX = from.positionX
            val posY = from.positionY
            val posZ = from.positionZ
            val rotationYaw = from.yaw
            val angle = (rotationYaw + 90f).toRadians()
            val distAlongLength = 0.0625f * 17f * -1f

            val pos = BlockPos.PooledMutableBlockPos.retain(posX, posY-0.5, posZ)
            val blockState = from.worldRef.getBlockState(pos)

            repeat(count) {
                // between -8s and 8s, s being the scaling factor
                val distAlongWidth = 0.0625f * 8f * (Math.random() * 2.0 - 1.0)
                val anchorX = posX + MathHelper.cos(angle) * distAlongLength + MathHelper.sin(angle) * distAlongWidth
                val anchorY = posY + 0.0625f * 4f
                val anchorZ = posZ + MathHelper.sin(angle) * distAlongLength - MathHelper.cos(angle) * distAlongWidth
                val particle = when {
                    blockState.material == Material.WATER -> Particles.DRIPPING_WATER
                    blockState.material == Material.LAVA -> Particles.DRIPPING_LAVA
                    else -> BlockParticleData(Particles.BLOCK, blockState)
                }
                from.worldRef.spawnParticle(particle, anchorX, anchorY, anchorZ, -from.velocityX, 1.0, -from.velocityZ)
            }
            pos.close()
        }
    }

    fun setStationary(boat: IControllable, newState: Boolean) {
        val isStationary = stationaryProperty[boat]
        stationaryProperty[boat] = newState
    }

    fun changeSpeed(boat: IControllable, speed: Float) {
        speedProperty[boat] = speed
    }

    fun isItemFuel(fuelItem: ItemStack) = getFuelTime(fuelItem) > 0

    fun isLockedByRedstone(boat: IControllable) = lockedByRedstoneProperty[boat]

    companion object {
        val SECONDS_TO_TICKS = 20
    }
}