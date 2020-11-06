package org.jglrxavpok.moarboats.common.entities

import net.minecraft.block.BlockState
import net.minecraft.dispenser.IDispenseItemBehavior
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ActionResultType
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fml.network.NetworkHooks
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.modules.OarEngineModule
import org.jglrxavpok.moarboats.common.network.SUtilityTileEntityUpdate
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import java.lang.Exception
import java.util.*

/**
 * Boat that can have a single passenger and a utility block (smoker, furnace, crafting table, etc.)
 */
abstract class UtilityBoatEntity<TE, C>(type: EntityType<out BasicBoatEntity>, world: World): BasicBoatEntity(type, world), INamedContainerProvider
    where TE: TileEntity, C: Container
{
    internal var boatType: BoatType = BoatType.OAK

    companion object {
        val InvalidPosition = BlockPos(0, -1, 0) // out of bounds so backing tile entities don't modify the world
    }

    override val entityID: Int
        get() = entityId
    override val modules: List<BoatModule>
        get() = emptyList()
    override val moduleRNG: Random
        get() = rand

    private val backingTileEntity: TE?

    init {
        backingTileEntity = initBackingTileEntity()
        this.preventEntitySpawning = true
    }

    abstract fun initBackingTileEntity(): TE?
    abstract fun getContainerType(): ContainerType<C>

    override fun controlBoat() {
        acceleration = 0.0f
        OarEngineModule.controlBoat(this)

        if(!blockedRotation) {
            this.rotationYaw += this.deltaRotation
        }
        if(!blockedMotion) {
            this.setMotion(velocityX + (MathHelper.sin(-this.rotationYaw * 0.017453292f) * acceleration).toDouble(), velocityY, (velocityZ + MathHelper.cos(this.rotationYaw * 0.017453292f) * acceleration).toDouble())
        } else {
            this.setMotion(0.0, motion.y, 0.0)
        }
    }

    fun sendTileEntityUpdate() {
        if(backingTileEntity == null)
            return
        if(!world.isRemote) {
            val data = backingTileEntity.write(CompoundNBT())
            MoarBoats.network.send(PacketDistributor.ALL.noArg(), SUtilityTileEntityUpdate(entityID, data))
        }
    }

    override fun getDisplayName(): ITextComponent {
        if(backingTileEntity is INamedContainerProvider) {
            return TranslationTextComponent("moarboats.container.utility_boat", backingTileEntity.displayName)
        }
        return super.getDisplayName()
    }

    override fun tick() {
        super.tick()
        if(backingTileEntity != null) {
            backingTileEntity.setLocation(world, InvalidPosition)
            if(backingTileEntity is ITickableTileEntity) {
                try {
                    backingTileEntity.tick()
                } catch (e: Exception) {
                    // shhhh, don't crash because you are not a block plz
                }
            }
        }
    }

    override fun processInitialInteract(player: PlayerEntity, hand: Hand): ActionResultType {
        if (super.processInitialInteract(player, hand) == ActionResultType.SUCCESS)
            return ActionResultType.SUCCESS
        if (world.isRemote)
            return ActionResultType.SUCCESS

        return openGuiIfPossible(player)
    }

    override fun openGuiIfPossible(player: PlayerEntity): ActionResultType {
        if(player is ServerPlayerEntity && getContainerType() != ContainerTypes.Empty) {
            NetworkHooks.openGui(player, this) {
                it.writeInt(entityID)
            }
            return ActionResultType.SUCCESS
        }
        return ActionResultType.FAIL
    }

    override fun canFitPassenger(passenger: Entity): Boolean {
        return this.passengers.isEmpty() && passenger is LivingEntity
    }

    override fun writeAdditional(compound: CompoundNBT) {
        super.writeAdditional(compound)
        compound.putString("boatType", boatType.getFullName())
        if(backingTileEntity != null) {
            compound.put("backingTileEntity", backingTileEntity.write(CompoundNBT()))
        }
    }

    override fun readAdditional(compound: CompoundNBT) {
        super.readAdditional(compound)
        boatType = BoatType.getTypeFromString(compound.getString("boatType"))
        backingTileEntity?.deserializeNBT(compound.getCompound("backingTileEntity"))
    }

    override fun isValidLiquidBlock(pos: BlockPos) = Fluids.isUsualLiquidBlock(world, pos)

    override fun canStartRiding(player: PlayerEntity, heldItem: ItemStack, hand: Hand): Boolean {
        return player !in passengers && heldItem.isEmpty
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        dropBaseBoat(killedByPlayerInCreative)
        val tileEntity = getBackingTileEntity()
        if(tileEntity is IInventory) {
            for (i in 0 until tileEntity.sizeInventory) {
                val stack = tileEntity.getStackInSlot(i)
                if(stack.isEmpty)
                    continue
                entityDropItem(stack.copy())
            }
        }
    }

    protected fun dropBaseBoat(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            entityDropItem(ItemStack(getBaseBoatItem()))
        }
    }

    fun getBaseBoatItem(): Item {
        return boatType.provideBoatItem()
    }

    override fun saveState(module: BoatModule, isLocal: Boolean) {
        // no-op
    }

    override fun getState(module: BoatModule, isLocal: Boolean): CompoundNBT {
        return CompoundNBT()
    }

    override fun getInventory(module: BoatModule): BoatModuleInventory {
        error("No module in this boat")
    }

    override fun dispense(behavior: IDispenseItemBehavior, stack: ItemStack, overridePosition: BlockPos?, overrideFacing: Direction?): ItemStack {
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

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            var f = 0.75f * 0.35f
            val f1 = ((if ( ! this.isAlive) 0.009999999776482582 else this.mountedYOffset) + passenger.yOffset).toFloat()

            val vec3d = Vector3d(f.toDouble(), 0.0, 0.0).rotateYaw(-(this.rotationYaw) * 0.017453292f - Math.PI.toFloat() / 2f)
            passenger.setPosition(this.x + vec3d.x, this.y + f1.toDouble(), this.z + vec3d.z)
            passenger.rotationYaw += this.deltaRotation
            passenger.rotationYawHead = passenger.rotationYawHead + this.deltaRotation
            this.applyYawToEntity(passenger)
        }
    }

    fun updateTileEntity(data: CompoundNBT) {
        backingTileEntity?.deserializeNBT(data)
    }

    fun getBoatType() = boatType
}