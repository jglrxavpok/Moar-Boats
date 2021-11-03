package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tileentity.AbstractFurnaceTileEntity
import net.minecraft.tileentity.BlastFurnaceTileEntity
import net.minecraft.tileentity.FurnaceTileEntity
import net.minecraft.tileentity.SmokerTileEntity
import net.minecraft.util.IIntArray
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.*
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.BlastFurnaceBoatItem
import org.jglrxavpok.moarboats.common.items.FurnaceBoatItem
import org.jglrxavpok.moarboats.common.items.SmokerBoatItem

private val furnaceDataField = ObfuscationReflectionHelper.findField(AbstractFurnaceTileEntity::class.java, "field_214013_b")
val BurnTimeField = ObfuscationReflectionHelper.findField(AbstractFurnaceTileEntity::class.java, "field_214018_j")

class FurnaceBoatEntity(world: World): AbstractFurnaceBoatEntity<FurnaceTileEntity, UtilityFurnaceContainer>(EntityEntries.FurnaceBoat, world) {

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vector3d.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): FurnaceTileEntity {
        return FurnaceTileEntity()
    }

    override fun getBoatItem(): Item {
        return FurnaceBoatItem[boatType]
    }

    override fun getContainerType(): ContainerType<UtilityFurnaceContainer> {
        return ContainerTypes.FurnaceBoat
    }

    override fun createMenu(p0: Int, p1: PlayerInventory, p2: PlayerEntity): Container? {
        val furnaceData = furnaceDataField[getBackingTileEntity()] as IIntArray
        return UtilityFurnaceContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.FURNACE))
        }
    }
}

class BlastFurnaceBoatEntity(world: World): AbstractFurnaceBoatEntity<BlastFurnaceTileEntity, UtilityBlastFurnaceContainer>(EntityEntries.BlastFurnaceBoat, world) {
    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vector3d.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): BlastFurnaceTileEntity {
        return BlastFurnaceTileEntity()
    }

    override fun getBoatItem(): Item {
        return BlastFurnaceBoatItem[boatType]
    }

    override fun getContainerType(): ContainerType<UtilityBlastFurnaceContainer> {
        return ContainerTypes.BlastFurnaceBoat
    }

    override fun createMenu(p0: Int, p1: PlayerInventory, p2: PlayerEntity): Container? {
        val furnaceData = furnaceDataField[getBackingTileEntity()] as IIntArray
        return UtilityBlastFurnaceContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.BLAST_FURNACE))
        }
    }
}

class SmokerBoatEntity(world: World): AbstractFurnaceBoatEntity<SmokerTileEntity, UtilitySmokerContainer>(EntityEntries.SmokerBoat, world) {
    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vector3d.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): SmokerTileEntity {
        return SmokerTileEntity()
    }

    override fun getBoatItem(): Item {
        return SmokerBoatItem[boatType]
    }

    override fun getContainerType(): ContainerType<UtilitySmokerContainer> {
        return ContainerTypes.SmokerBoat
    }

    override fun createMenu(p0: Int, p1: PlayerInventory, p2: PlayerEntity): Container? {
        val furnaceData = furnaceDataField[getBackingTileEntity()] as IIntArray
        return UtilitySmokerContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.SMOKER))
        }
    }
}

abstract class AbstractFurnaceBoatEntity<T: AbstractFurnaceTileEntity, C: AbstractFurnaceContainer>(type: EntityType<out AbstractFurnaceBoatEntity<T, C>>, world: World): UtilityBoatEntity<T, C>(type, world) {

    private var timeUntilUpdate = MoarBoatsConfig.furnaceBoats.stateUpdateInterval.get()

    override fun tick() {
        val wasBurning = isFurnaceLit()
        super.tick()
        val isBurning = isFurnaceLit()
        if(!world.isClientSide && (isBurning != wasBurning || isBurning && timeUntilUpdate-- == 0)) {
            sendTileEntityUpdate()
            timeUntilUpdate = MoarBoatsConfig.furnaceBoats.stateUpdateInterval.get()
        }
    }

    fun isFurnaceLit() = BurnTimeField.getInt(getBackingTileEntity()) > 0

}