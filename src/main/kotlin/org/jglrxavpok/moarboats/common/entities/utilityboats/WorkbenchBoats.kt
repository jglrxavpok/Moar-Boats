package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.containers.*
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.*

class CraftingTableBoatEntity(world: Level): UtilityBoatEntity<BlockEntity, UtilityWorkbenchContainer>(EntityEntries.CraftingTableBoat, world) {

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

    override fun getContainerType(): MenuType<UtilityWorkbenchContainer> {
        return ContainerTypes.CraftingBoat
    }

    override fun getBoatItem(): Item {
        return CraftingTableBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityWorkbenchContainer? {
        return UtilityWorkbenchContainer(windowID, inv, ContainerLevelAccess.create(player.level, player.blockPosition()))
    }


    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.crafting"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.CRAFTING_TABLE))
        }
    }
}

class GrindstoneBoatEntity(world: Level): UtilityBoatEntity<BlockEntity, UtilityGrindstoneContainer>(EntityEntries.GrindstoneBoat, world) {

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

    override fun getContainerType(): MenuType<UtilityGrindstoneContainer> {
        return ContainerTypes.GrindstoneBoat
    }

    override fun getBoatItem(): Item {
        return GrindstoneBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityGrindstoneContainer? {
        return UtilityGrindstoneContainer(windowID, inv, ContainerLevelAccess.create(player.level, player.blockPosition()))
    }


    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.grindstone"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.GRINDSTONE))
        }
    }

}

class LoomBoatEntity(world: Level): UtilityBoatEntity<BlockEntity, UtilityLoomContainer>(EntityEntries.LoomBoat, world) {

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

    override fun getContainerType(): MenuType<UtilityLoomContainer> {
        return ContainerTypes.LoomBoat
    }

    override fun getBoatItem(): Item {
        return LoomBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityLoomContainer? {
        return UtilityLoomContainer(windowID, inv, ContainerLevelAccess.create(player.level, player.blockPosition()))
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.loom"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.LOOM))
        }
    }
}

class CartographyTableBoatEntity(world: Level): UtilityBoatEntity<BlockEntity, UtilityCartographyTableContainer>(EntityEntries.CartographyTableBoat, world) {

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

    override fun getContainerType(): MenuType<UtilityCartographyTableContainer> {
        return ContainerTypes.CartographyTableBoat
    }

    override fun getBoatItem(): Item {
        return CartographyTableBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityCartographyTableContainer? {
        return UtilityCartographyTableContainer(windowID, inv, ContainerLevelAccess.create(player.level, player.blockPosition()))
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.cartography_table"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.CARTOGRAPHY_TABLE))
        }
    }
}

class StonecutterBoatEntity(world: Level): UtilityBoatEntity<BlockEntity, UtilityStonecutterContainer>(EntityEntries.StonecutterBoat, world) {

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

    override fun getContainerType(): MenuType<UtilityStonecutterContainer> {
        return ContainerTypes.StonecutterBoat
    }

    override fun getBoatItem(): Item {
        return StonecutterBoatItem[boatType]
    }

    override fun createMenu(windowID: Int, inv: Inventory, player: Player): UtilityStonecutterContainer? {
        return UtilityStonecutterContainer(windowID, inv, ContainerLevelAccess.create(player.level, player.blockPosition()))
    }

    override fun getDisplayName(): Component {
        return Component.translatable("moarboats.container.utility_boat", Component.translatable("container.stonecutter"))
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        super.dropItemsOnDeath(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.STONECUTTER))
        }
    }
}