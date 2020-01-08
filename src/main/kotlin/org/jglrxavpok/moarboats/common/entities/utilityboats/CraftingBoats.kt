package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IWorldPosCallable
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.containers.*
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.CartographyTableBoatItem
import org.jglrxavpok.moarboats.common.items.CraftingTableBoatItem
import org.jglrxavpok.moarboats.common.items.GrindstoneBoatItem
import org.jglrxavpok.moarboats.common.items.LoomBoatItem

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
        return CraftingTableBoatItem
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityWorkbenchContainer? {
        return UtilityWorkbenchContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
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
        return GrindstoneBoatItem
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityGrindstoneContainer? {
        return UtilityGrindstoneContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
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
        return LoomBoatItem
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityLoomContainer? {
        return UtilityLoomContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
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
        return CartographyTableBoatItem
    }

    override fun createMenu(windowID: Int, inv: PlayerInventory, player: PlayerEntity): UtilityCartographyTableContainer? {
        return UtilityCartographyTableContainer(windowID, inv, IWorldPosCallable.of(player.world, player.position))
    }
}