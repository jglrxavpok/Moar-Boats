package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ChestContainer
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.tileentity.ChestTileEntity
import net.minecraft.tileentity.ShulkerBoxTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.containers.UtilityChestContainer
import org.jglrxavpok.moarboats.common.containers.UtilityShulkerContainer
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.ChestBoatItem

class ChestBoatEntity(world: World): UtilityBoatEntity<ChestTileEntity, UtilityChestContainer>(EntityEntries.ChestBoat, world) {

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPosition(x, y, z)
        this.motion = Vec3d.ZERO
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
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

}

class ShulkerBoatEntity(world: World): UtilityBoatEntity<ShulkerBoxTileEntity, UtilityShulkerContainer>(EntityEntries.ShulkerBoat, world) {

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPosition(x, y, z)
        this.motion = Vec3d.ZERO
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
    }

    override fun initBackingTileEntity(): ShulkerBoxTileEntity? {
        return ShulkerBoxTileEntity()
    }

    override fun getContainerType(): ContainerType<UtilityShulkerContainer> {
        return ContainerTypes.ShulkerBoat
    }

    override fun getBoatItem(): Item {
        return ChestBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityShulkerContainer? {
        return UtilityShulkerContainer(windowID, inv, getBackingTileEntity()!!)
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("container.shulkerBox"))
    }

}

class EnderChestBoatEntity(world: World): UtilityBoatEntity<TileEntity, ChestContainer>(EntityEntries.EnderChestBoat, world) {

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPosition(x, y, z)
        this.motion = Vec3d.ZERO
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
    }

    override fun initBackingTileEntity(): TileEntity? {
        return null
    }

    override fun getContainerType(): ContainerType<ChestContainer> {
        return ContainerTypes.EnderChestBoat
    }

    override fun getBoatItem(): Item {
        return ChestBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): ChestContainer? {
        return ChestContainer.createGeneric9X3(windowID, inv, player.inventoryEnderChest)
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("block.minecraft.ender_chest"))
    }

}