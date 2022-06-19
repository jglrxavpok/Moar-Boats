package org.jglrxavpok.moarboats.common.entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.dispenser.DispenseItemBehavior
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.modules.OarEngineModule
import org.jglrxavpok.moarboats.common.network.SUtilityTileEntityUpdate
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.common.tileentity.ITickableTileEntity
import org.jglrxavpok.moarboats.extensions.Fluids
import java.util.*

/**
 * Boat that can have a single passenger and a utility block (smoker, furnace, crafting table, etc.)
 */
abstract class UtilityBoatEntity<TE, C>(type: EntityType<out BasicBoatEntity>, world: Level): BasicBoatEntity(type, world), MenuProvider
    where TE: BlockEntity, C: AbstractContainerMenu
{
    internal var boatType: BoatType = BoatType.OAK

    companion object {
        val InvalidPosition = BlockPos(0, -1, 0) // out of bounds so backing tile entities don't modify the world
    }

    override val entityID: Int
        get() = id
    override val modules: List<BoatModule>
        get() = emptyList()
    override val moduleRNG: RandomSource
        get() = this.random

    private val backingTileEntity: TE?

    init {
        backingTileEntity = initBackingTileEntity()
        this.blocksBuilding = true
    }

    abstract fun initBackingTileEntity(): TE?
    abstract fun getContainerType(): MenuType<C>

    override fun controlBoat() {
        acceleration = 0.0f
        OarEngineModule.controlBoat(this)

        if(!blockedRotation) {
            this.yRot += this.deltaRotation
        }
        if(!blockedMotion) {
            this.setDeltaMovement(velocityX + (Mth.sin(-this.yRot * 0.017453292f) * acceleration).toDouble(), velocityY, (velocityZ + Mth.cos(this.yRot * 0.017453292f) * acceleration).toDouble())
        } else {
            this.setDeltaMovement(0.0, deltaMovement.y, 0.0)
        }
    }

    fun sendTileEntityUpdate() {
        if(backingTileEntity == null)
            return
        if(!world.isClientSide) {
            val data = backingTileEntity.saveWithoutMetadata()
            MoarBoats.network.send(PacketDistributor.ALL.noArg(), SUtilityTileEntityUpdate(entityID, data))
        }
    }

    override fun getDisplayName(): Component {
        if(backingTileEntity is MenuProvider) {
            return Component.translatable("moarboats.container.utility_boat", backingTileEntity.displayName)
        }
        return super.getDisplayName()
    }

    override fun tick() {
        super.tick()
        if(backingTileEntity != null) {
            backingTileEntity.level = world
            // TODO 1.19 - ITickableTileEntity is now a MoarBoats construct
            if(backingTileEntity is ITickableTileEntity) {
                try {
                    backingTileEntity.tick()
                } catch (e: Exception) {
                    // shhhh, don't crash because you are not a block plz
                }
            }
        }
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        if (super.interact(player, hand) == InteractionResult.SUCCESS)
            return InteractionResult.SUCCESS
        if (world.isClientSide)
            return InteractionResult.SUCCESS

        return openGuiIfPossible(player)
    }

    override fun openGuiIfPossible(player: Player): InteractionResult {
        if(player is ServerPlayer && getContainerType() != ContainerTypes.Empty.get()) {
            NetworkHooks.openGui(player, this) {
                it.writeInt(entityID)
            }
            return InteractionResult.SUCCESS
        }
        return InteractionResult.FAIL
    }

    override fun canAddPassenger(passenger: Entity): Boolean {
        return this.passengers.isEmpty() && passenger is LivingEntity
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putString("boatType", boatType.getFullName())
        if(backingTileEntity != null) {
            compound.put("backingTileEntity", backingTileEntity.saveWithoutMetadata())
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        boatType = BoatType.getTypeFromString(compound.getString("boatType"))
        backingTileEntity?.deserializeNBT(compound.getCompound("backingTileEntity"))
    }

    override fun isValidLiquidBlock(pos: BlockPos) = Fluids.isUsualLiquidBlock(world, pos)

    override fun canStartRiding(player: Player, heldItem: ItemStack, hand: InteractionHand): Boolean {
        return player !in passengers && heldItem.isEmpty
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        dropBaseBoat(killedByPlayerInCreative)
        val tileEntity = getBackingTileEntity()
        if(tileEntity is Container) {
            for (i in 0 until tileEntity.containerSize) {
                val stack = tileEntity.getItem(i)
                if(stack.isEmpty)
                    continue
                spawnAtLocation(stack.copy())
            }
        }
    }

    protected fun dropBaseBoat(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            getBaseBoatItem()?.let { item -> spawnAtLocation(ItemStack(item)) }
        }
    }

    fun getBaseBoatItem(): Item? {
        return boatType.provideBoatItem()
    }

    override fun saveState(module: BoatModule, isLocal: Boolean) {
        // no-op
    }

    override fun getState(module: BoatModule, isLocal: Boolean): CompoundTag {
        return CompoundTag()
    }

    override fun getInventory(module: BoatModule): BoatModuleInventory {
        error("No module in this boat")
    }

    override fun dispense(behavior: DispenseItemBehavior, stack: ItemStack, overridePosition: BlockPos?, overrideFacing: Direction?): ItemStack {
        error("No dispenser in this boat")
    }

    override fun reorientate(overrideFacing: Direction): Direction {
        error("No dispenser in this boat")
    }

    override fun <T> contains(property: BoatProperty<T>): Boolean {
        return false
    }

    fun getBackingTileEntity() = backingTileEntity

    override fun getOwnerIdOrNull(): UUID? {
        return null
    }

    override fun getOwnerNameOrNull(): String? {
        return null
    }

    override fun getControllingPassenger(): Entity? {
        return if(passengers.isEmpty()) null else passengers[0]
    }

    override fun positionRider(passenger: Entity) {
        if (this.hasPassenger(passenger)) {
            var f = 0.75f * 0.35f
            val f1 = ((if ( ! this.isAlive) 0.009999999776482582 else this.myRidingOffset) + passenger.passengersRidingOffset).toFloat()

            val vec3d = Vec3(f.toDouble(), 0.0, 0.0).yRot(-(this.yRot) * 0.017453292f - Math.PI.toFloat() / 2f)
            passenger.setPos(this.x + vec3d.x, this.y + f1.toDouble(), this.z + vec3d.z)
            passenger.yRot += this.deltaRotation
            passenger.yHeadRot = passenger.yHeadRot + this.deltaRotation
            this.applyYawToEntity(passenger)
        }
    }

    fun updateTileEntity(data: CompoundTag) {
        backingTileEntity?.deserializeNBT(data)
    }

    fun getBoatType() = boatType
}