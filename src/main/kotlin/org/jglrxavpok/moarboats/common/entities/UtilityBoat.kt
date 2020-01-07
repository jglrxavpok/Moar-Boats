package org.jglrxavpok.moarboats.common.entities

import net.minecraft.block.BlockState
import net.minecraft.dispenser.IDispenseItemBehavior
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.common.modules.OarEngineModule
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import java.util.*

/**
 * Boat that can have a single passenger and a utility block (smoker, furnace, crafting table, etc.)
 */
abstract class UtilityBoat<TE>(type: EntityType<out BasicBoatEntity>, world: World): BasicBoatEntity(type, world)
    where TE: TileEntity
{
    override val entityID: Int
        get() = entityId
    override val modules: List<BoatModule>
        get() = emptyList()
    override val moduleRNG: Random
        get() = rand

    private val backingTileEntity: TE

    init {
        backingTileEntity = initBackingTileEntity()
        this.preventEntitySpawning = true
    }

    constructor(type: EntityType<out BasicBoatEntity>, level: World, x: Double, y: Double, z: Double): this(type, level) {
        this.setPosition(x, y, z)
        this.motion = Vec3d.ZERO
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
    }

    abstract fun initBackingTileEntity(): TE

    override fun controlBoat() {
        OarEngineModule.controlBoat(this)
    }

    override fun tick() {
        super.tick()
        if(backingTileEntity is ITickableTileEntity) {
            backingTileEntity.tick()
        }
    }

    override fun processInitialInteract(player: PlayerEntity, hand: Hand): Boolean {
        if (super.processInitialInteract(player, hand))
            return true
        if (world.isRemote)
            return true

        if(player.getHeldItem(hand).isEmpty && canFitPassenger(player)) {
            player.startRiding(this)
        } else {
            // TODO: open menu
        }

        return true
    }

    override fun canFitPassenger(passenger: Entity): Boolean {
        return this.passengers.isEmpty() && passenger is LivingEntity
    }

    override fun writeAdditional(compound: CompoundNBT) {
        super.writeAdditional(compound)
        backingTileEntity.write(compound)
    }

    override fun readAdditional(compound: CompoundNBT) {
        super.readAdditional(compound)
        backingTileEntity.read(compound)
    }

    override fun isValidLiquidBlock(pos: BlockPos) = Fluids.isUsualLiquidBlock(world, pos)

    override fun canStartRiding(player: PlayerEntity, heldItem: ItemStack, hand: Hand): Boolean {
        return player !in passengers
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        entityDropItem(ItemStack(getBoatItem()))
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

    override fun <T : TileEntity?> getBlockTileEntity() = backingTileEntity as T

    override fun getOwnerIdOrNull(): UUID? {
        return null
    }

    override fun getOwnerNameOrNull(): String? {
        return null
    }

    override fun getX() = posX
    override fun getY() = posY
    override fun getZ() = posZ

    override fun getBlockState(): BlockState? {
        return null
    }

    override fun getWorld(): World {
        return world
    }

    override fun getBlockPos(): BlockPos {
        return BlockPos.ZERO
    }
}