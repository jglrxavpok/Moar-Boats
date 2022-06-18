package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.AbstractFurnaceMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity
import net.minecraft.world.level.block.entity.FurnaceBlockEntity
import net.minecraft.world.level.block.entity.SmokerBlockEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.fml.util.ObfuscationReflectionHelper
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.UtilityBlastFurnaceContainer
import org.jglrxavpok.moarboats.common.containers.UtilityFurnaceContainer
import org.jglrxavpok.moarboats.common.containers.UtilitySmokerContainer
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.BlastFurnaceBoatItem
import org.jglrxavpok.moarboats.common.items.FurnaceBoatItem
import org.jglrxavpok.moarboats.common.items.SmokerBoatItem

// TODO: Access transformer
private val furnaceDataField = ObfuscationReflectionHelper.findField(AbstractFurnaceBlockEntity::class.java, "field_214013_b")
val BurnTimeField = ObfuscationReflectionHelper.findField(AbstractFurnaceBlockEntity::class.java, "field_214018_j")

class FurnaceBoatEntity(world: Level): AbstractFurnaceBoatEntity<FurnaceBlockEntity, UtilityFurnaceContainer>(EntityEntries.FurnaceBoat, world) {

    constructor(level: Level, x: Double, y: Double, z: Double): this(level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): FurnaceBlockEntity {
        return FurnaceBlockEntity()
    }

    override fun getBoatItem(): Item {
        return FurnaceBoatItem[boatType]
    }

    override fun getContainerType(): MenuType<UtilityFurnaceContainer> {
        return ContainerTypes.FurnaceBoat
    }

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? {
        val furnaceData = furnaceDataField[getBackingTileEntity()] as ContainerData
        return UtilityFurnaceContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.FURNACE))
        }
    }
}

class BlastFurnaceBoatEntity(world: Level): AbstractFurnaceBoatEntity<BlastFurnaceBlockEntity, UtilityBlastFurnaceContainer>(EntityEntries.BlastFurnaceBoat, world) {
    constructor(level: Level, x: Double, y: Double, z: Double): this(level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): BlastFurnaceBlockEntity {
        return BlastFurnaceBlockEntity()
    }

    override fun getBoatItem(): Item {
        return BlastFurnaceBoatItem[boatType]
    }

    override fun getContainerType(): MenuType<UtilityBlastFurnaceContainer> {
        return ContainerTypes.BlastFurnaceBoat
    }

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? {
        val furnaceData = furnaceDataField[getBackingTileEntity()] as ContainerData
        return UtilityBlastFurnaceContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.BLAST_FURNACE))
        }
    }
}

class SmokerBoatEntity(world: Level): AbstractFurnaceBoatEntity<SmokerBlockEntity, UtilitySmokerContainer>(EntityEntries.SmokerBoat, world) {
    constructor(level: Level, x: Double, y: Double, z: Double): this(level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): SmokerBlockEntity {
        return SmokerBlockEntity()
    }

    override fun getBoatItem(): Item {
        return SmokerBoatItem[boatType]
    }

    override fun getContainerType(): MenuType<UtilitySmokerContainer> {
        return ContainerTypes.SmokerBoat
    }

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? {
        val furnaceData = furnaceDataField[getBackingTileEntity()] as ContainerData
        return UtilitySmokerContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.SMOKER))
        }
    }
}

abstract class AbstractFurnaceBoatEntity<T: AbstractFurnaceBlockEntity, C: AbstractFurnaceMenu>(type: EntityType<out AbstractFurnaceBoatEntity<T, C>>, world: Level): UtilityBoatEntity<T, C>(type, world) {

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