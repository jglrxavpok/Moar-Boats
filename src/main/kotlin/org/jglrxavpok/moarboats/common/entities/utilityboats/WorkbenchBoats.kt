package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IWorldPosCallable
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.containers.*
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.*

class CraftingTableBoatEntity(world: World): UtilityBoatEntity<TileEntity, UtilityWorkbenchContainer>(EntityEntries.CraftingTableBoat, world) {

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

    override fun getContainerType(): ContainerType<UtilityWorkbenchContainer> {
        return ContainerTypes.CraftingBoat
    }

    override fun getBoatItem(): Item {
        return CraftingTableBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityWorkbenchContainer? {
        return UtilityWorkbenchContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
    }


    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("container.crafting"))
    }

}

class GrindstoneBoatEntity(world: World): UtilityBoatEntity<TileEntity, UtilityGrindstoneContainer>(EntityEntries.GrindstoneBoat, world) {

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

    override fun getContainerType(): ContainerType<UtilityGrindstoneContainer> {
        return ContainerTypes.GrindstoneBoat
    }

    override fun getBoatItem(): Item {
        return GrindstoneBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityGrindstoneContainer? {
        return UtilityGrindstoneContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
    }


    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("container.grindstone"))
    }

}

class LoomBoatEntity(world: World): UtilityBoatEntity<TileEntity, UtilityLoomContainer>(EntityEntries.LoomBoat, world) {

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

    override fun getContainerType(): ContainerType<UtilityLoomContainer> {
        return ContainerTypes.LoomBoat
    }

    override fun getBoatItem(): Item {
        return LoomBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityLoomContainer? {
        return UtilityLoomContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("container.loom"))
    }

}

class CartographyTableBoatEntity(world: World): UtilityBoatEntity<TileEntity, UtilityCartographyTableContainer>(EntityEntries.CartographyTableBoat, world) {

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

    override fun getContainerType(): ContainerType<UtilityCartographyTableContainer> {
        return ContainerTypes.CartographyTableBoat
    }

    override fun getBoatItem(): Item {
        return CartographyTableBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityCartographyTableContainer? {
        return UtilityCartographyTableContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("container.cartography_table"))
    }
}

class StonecutterBoatEntity(world: World): UtilityBoatEntity<TileEntity, UtilityStonecutterContainer>(EntityEntries.StonecutterBoat, world) {

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

    override fun getContainerType(): ContainerType<UtilityStonecutterContainer> {
        return ContainerTypes.StonecutterBoat
    }

    override fun getBoatItem(): Item {
        return StonecutterBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityStonecutterContainer? {
        return UtilityStonecutterContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("moarboats.container.utility_boat", TranslationTextComponent("container.stonecutter"))
    }
}