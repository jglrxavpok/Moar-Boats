package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.entity.item.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ChestContainer
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.DyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.ChestTileEntity
import net.minecraft.tileentity.ShulkerBoxTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.UtilityChestContainer
import org.jglrxavpok.moarboats.common.containers.UtilityShulkerContainer
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.ChestBoatItem
import org.jglrxavpok.moarboats.common.items.EnderChestBoatItem
import org.jglrxavpok.moarboats.common.items.ShulkerBoatItem

class ChestBoatEntity(world: World): UtilityBoatEntity<ChestTileEntity, UtilityChestContainer>(EntityEntries.ChestBoat, world) {

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPosition(x, y, z)
        this.deltaMovement = Vector3d.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): ChestTileEntity? {
        return ChestTileEntity()
    }

    override fun getContainerType(): ContainerType<UtilityChestContainer> {
        return ContainerTypes.ChestBoat
    }

    override fun getBoatItem(): Item {
        return ChestBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityChestContainer? {
        return UtilityChestContainer(windowID, inv, getBackingTileEntity()!!)
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("container.chest"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.CHEST))
        }
    }

}

class ShulkerBoatEntity(world: World): UtilityBoatEntity<ShulkerBoxTileEntity, UtilityShulkerContainer>(EntityEntries.ShulkerBoat, world) {

    internal var dyeColor: DyeColor? = null

    constructor(color: DyeColor?, level: World, x: Double, y: Double, z: Double): this(level) {
        this.dyeColor = color
        this.setPosition(x, y, z)
        this.deltaMovement = Vector3d.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): ShulkerBoxTileEntity? {
        return ShulkerBoxTileEntity(dyeColor)
    }

    override fun getContainerType(): ContainerType<UtilityShulkerContainer> {
        return ContainerTypes.ShulkerBoat
    }

    override fun getBoatItem(): Item {
        return ShulkerBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityShulkerContainer? {
        return UtilityShulkerContainer(windowID, inv, getBackingTileEntity()!!)
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("container.shulkerBox"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        dropBaseBoat(killedByPlayerInCreative)
        val tileEntity = getBackingTileEntity()!!
        if(!killedByPlayerInCreative || !tileEntity.isEmpty) {
            val stack = ShulkerBoxBlock.getColoredItemStack(dyeColor)
            val nbt = tileEntity.saveToTag(CompoundNBT())
            if (!nbt.isEmpty) {
                stack.setTagInfo("BlockEntityTag", nbt)
            }

            if (tileEntity.hasCustomName()) {
                stack.displayName = tileEntity.customName
            }
            spawnAtLocation(stack)
        }
    }

    override fun readAdditional(compound: CompoundNBT) {
        super.readAdditional(compound)
        dyeColor = if("Color" in compound) {
            DyeColor.byName(compound.getString("Color"), null)
        } else {
            null
        }
    }

    override fun writeAdditional(compound: CompoundNBT) {
        super.writeAdditional(compound)
        if(dyeColor != null) {
            compound.putString("Color", dyeColor?.getName())
        }
    }
}

class EnderChestBoatEntity(world: World): UtilityBoatEntity<TileEntity, ChestContainer>(EntityEntries.EnderChestBoat, world) {

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPosition(x, y, z)
        this.deltaMovement = Vector3d.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): TileEntity? {
        return null
    }

    override fun getContainerType(): ContainerType<ChestContainer> {
        return ContainerTypes.EnderChestBoat
    }

    override fun getBoatItem(): Item {
        return EnderChestBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): ChestContainer? {
        return ChestContainer.threeRows(windowID, inv, player.enderChestInventory)
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("block.minecraft.ender_chest"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.ENDER_CHEST))
        }
    }
}