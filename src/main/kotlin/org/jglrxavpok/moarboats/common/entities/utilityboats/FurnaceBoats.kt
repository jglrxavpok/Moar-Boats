package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.AbstractFurnaceMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AbstractFurnaceBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.*
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PlayMessages
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.UtilityBlastFurnaceContainer
import org.jglrxavpok.moarboats.common.containers.UtilityFurnaceContainer
import org.jglrxavpok.moarboats.common.containers.UtilitySmokerContainer
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity

class FurnaceBoatEntity(entityType: EntityType<out FurnaceBoatEntity>, world: Level): AbstractFurnaceBoatEntity<FurnaceBlockEntity, UtilityFurnaceContainer>(entityType, world) {

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out FurnaceBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out FurnaceBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): FurnaceBlockEntity {
        return FurnaceBlockEntity(InvalidPosition, Blocks.FURNACE.defaultBlockState())
    }

    override fun getBoatItem(): Item {
        return MBItems.FurnaceBoats[boatType]!!.get()
    }

    override fun getContainerType(): MenuType<UtilityFurnaceContainer> {
        return ContainerTypes.FurnaceBoat.get()
    }

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? {
        val furnaceData = getBackingTileEntity()!!.dataAccess
        return UtilityFurnaceContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }
}

class BlastFurnaceBoatEntity(entityType: EntityType<out BlastFurnaceBoatEntity>, world: Level): AbstractFurnaceBoatEntity<BlastFurnaceBlockEntity, UtilityBlastFurnaceContainer>(entityType, world) {
    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out BlastFurnaceBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out BlastFurnaceBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): BlastFurnaceBlockEntity {
        return BlastFurnaceBlockEntity(InvalidPosition, Blocks.BLAST_FURNACE.defaultBlockState())
    }

    override fun getBoatItem(): Item {
        return MBItems.BlastFurnaceBoats[boatType]!!.get()
    }

    override fun getContainerType(): MenuType<UtilityBlastFurnaceContainer> {
        return ContainerTypes.BlastFurnaceBoat.get()
    }

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? {
        val furnaceData = getBackingTileEntity()!!.dataAccess
        return UtilityBlastFurnaceContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }
}

class SmokerBoatEntity(entityType: EntityType<out SmokerBoatEntity>, world: Level): AbstractFurnaceBoatEntity<SmokerBlockEntity, UtilitySmokerContainer>(entityType, world) {
    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out SmokerBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out SmokerBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): SmokerBlockEntity {
        return SmokerBlockEntity(InvalidPosition, Blocks.SMOKER.defaultBlockState())
    }

    override fun getBoatItem(): Item {
        return MBItems.SmokerBoats[boatType]!!.get()
    }

    override fun getContainerType(): MenuType<UtilitySmokerContainer> {
        return ContainerTypes.SmokerBoat.get()
    }

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? {
        val furnaceData = getBackingTileEntity()!!.dataAccess
        return UtilitySmokerContainer(p0, p1, getBackingTileEntity(), furnaceData)
    }
}

abstract class AbstractFurnaceBoatEntity<T: AbstractFurnaceBlockEntity, C: AbstractFurnaceMenu>(type: EntityType<out AbstractFurnaceBoatEntity<T, C>>, world: Level): UtilityBoatEntity<T, C>(type, world) {

    private var timeUntilUpdate = MoarBoatsConfig.furnaceBoats.stateUpdateInterval.get()
    private val invalidBlockPos = BlockPos(0, level.minBuildHeight-100, 0)


    override fun tick() {
        val wasBurning = isFurnaceLit()
        super.tick()
        val isBurning = isFurnaceLit()
        if(!world.isClientSide && (isBurning != wasBurning || isBurning && timeUntilUpdate-- == 0)) {
            sendTileEntityUpdate()
            timeUntilUpdate = MoarBoatsConfig.furnaceBoats.stateUpdateInterval.get()
        }
    }

    override fun tickBlockEntity() {
        val furnaceBlockState = Blocks.FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.LIT, isFurnaceLit())
        AbstractFurnaceBlockEntity.serverTick(level, invalidBlockPos, furnaceBlockState, getBackingTileEntity()!!)
    }

    fun isFurnaceLit() = getBackingTileEntity()!!.isLit

}