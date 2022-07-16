package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.ShulkerBoxBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PlayMessages
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.UtilityChestContainer
import org.jglrxavpok.moarboats.common.containers.UtilityShulkerContainer
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.ChestBoatItem
import org.jglrxavpok.moarboats.common.items.EnderChestBoatItem
import org.jglrxavpok.moarboats.common.items.ShulkerBoatItem

class ChestBoatEntity(entityType: EntityType<out ChestBoatEntity>, world: Level): UtilityBoatEntity<ChestBlockEntity, UtilityChestContainer>(entityType, world) {

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out ChestBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out ChestBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): ChestBlockEntity? {
        return ChestBlockEntity(InvalidPosition, Blocks.CHEST.defaultBlockState())
    }

    override fun getContainerType(): MenuType<UtilityChestContainer> {
        return ContainerTypes.ChestBoat.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.ChestBoats[boatType]!!.get()
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityChestContainer? {
        return UtilityChestContainer(windowID, inv, getBackingTileEntity()!!)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.chest"))
    }

}

class ShulkerBoatEntity(entityType: EntityType<out ShulkerBoatEntity>, world: Level): UtilityBoatEntity<ShulkerBoxBlockEntity, UtilityShulkerContainer>(entityType, world) {

    internal var dyeColor: DyeColor? = null

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out ShulkerBoatEntity>, null, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out ShulkerBoatEntity>, color: DyeColor?, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
        this.dyeColor = color
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): ShulkerBoxBlockEntity? {
        return ShulkerBoxBlockEntity(dyeColor, InvalidPosition, Blocks.SHULKER_BOX.defaultBlockState())
    }

    override fun getContainerType(): MenuType<UtilityShulkerContainer> {
        return ContainerTypes.ShulkerBoat.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.ShulkerBoats[boatType]!!.get()
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityShulkerContainer? {
        return UtilityShulkerContainer(windowID, inv, getBackingTileEntity()!!)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.shulkerBox"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            getBaseBoatItem()?.let { item -> spawnAtLocation(item) }
        }
        val tileEntity = getBackingTileEntity()!!
        if(!killedByPlayerInCreative || !tileEntity.isEmpty) {
            val stack = ShulkerBoxBlock.getColoredItemStack(dyeColor)
            val nbt = tileEntity.saveWithoutMetadata()
            if (!nbt.isEmpty) {
                stack.addTagElement("BlockEntityTag", nbt)
            }

            if (tileEntity.hasCustomName()) {
                stack.hoverName = tileEntity.customName
            }
            spawnAtLocation(stack)
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        dyeColor = if("Color" in compound) {
            DyeColor.byName(compound.getString("Color"), null)
        } else {
            null
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        if(dyeColor != null) {
            compound.putString("Color", dyeColor?.getName())
        }
    }
}

class EnderChestBoatEntity(entityType: EntityType<out EnderChestBoatEntity>, world: Level): UtilityBoatEntity<BlockEntity, ChestMenu>(entityType, world) {

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out EnderChestBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out EnderChestBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): BlockEntity? {
        return null
    }

    override fun getContainerType(): MenuType<ChestMenu> {
        return ContainerTypes.EnderChestBoat.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.EnderChestBoats[boatType]!!.get()
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): ChestMenu? {
        return ChestMenu.threeRows(windowID, inv, player.enderChestInventory)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("block.minecraft.ender_chest"))
    }
}