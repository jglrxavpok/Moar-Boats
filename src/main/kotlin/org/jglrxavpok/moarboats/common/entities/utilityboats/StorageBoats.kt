package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.ShulkerBoxBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity
import net.minecraft.world.phys.Vec3
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.UtilityChestContainer
import org.jglrxavpok.moarboats.common.containers.UtilityShulkerContainer
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.ChestBoatItem
import org.jglrxavpok.moarboats.common.items.EnderChestBoatItem
import org.jglrxavpok.moarboats.common.items.ShulkerBoatItem

class ChestBoatEntity(world: Level): UtilityBoatEntity<ChestBlockEntity, UtilityChestContainer>(EntityEntries.ChestBoat, world) {

    constructor(level: Level, x: Double, y: Double, z: Double): this(level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): ChestBlockEntity? {
        return ChestBlockEntity()
    }

    override fun getContainerType(): MenuType<UtilityChestContainer> {
        return ContainerTypes.ChestBoat
    }

    override fun getBoatItem(): Item {
        return ChestBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityChestContainer? {
        return UtilityChestContainer(windowID, inv, getBackingTileEntity()!!)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.chest"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.CHEST))
        }
    }

}

class ShulkerBoatEntity(world: Level): UtilityBoatEntity<ShulkerBoxBlockEntity, UtilityShulkerContainer>(EntityEntries.ShulkerBoat, world) {

    internal var dyeColor: DyeColor? = null

    constructor(color: DyeColor?, level: Level, x: Double, y: Double, z: Double): this(level) {
        this.dyeColor = color
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): ShulkerBoxBlockEntity? {
        return ShulkerBoxBlockEntity(dyeColor)
    }

    override fun getContainerType(): MenuType<UtilityShulkerContainer> {
        return ContainerTypes.ShulkerBoat
    }

    override fun getBoatItem(): Item {
        return ShulkerBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityShulkerContainer? {
        return UtilityShulkerContainer(windowID, inv, getBackingTileEntity()!!)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.shulkerBox"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        dropBaseBoat(killedByPlayerInCreative)
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

class EnderChestBoatEntity(world: Level): UtilityBoatEntity<BlockEntity, ChestMenu>(EntityEntries.EnderChestBoat, world) {

    constructor(level: Level, x: Double, y: Double, z: Double): this(level) {
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
        return ContainerTypes.EnderChestBoat
    }

    override fun getBoatItem(): Item {
        return EnderChestBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): ChestMenu? {
        return ChestMenu.threeRows(windowID, inv, player.enderChestInventory)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("block.minecraft.ender_chest"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.ENDER_CHEST))
        }
    }
}