package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EntityType
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
import net.minecraftforge.network.PlayMessages
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.containers.*
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.*

class CraftingTableBoatEntity(entityType: EntityType<out CraftingTableBoatEntity>, world: Level): UtilityBoatEntity<BlockEntity, UtilityWorkbenchContainer>(entityType, world) {

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out CraftingTableBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out CraftingTableBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
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
        return ContainerTypes.CraftingBoat.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.CraftingTableBoats[boatType]!!.get()
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

class GrindstoneBoatEntity(entityType: EntityType<out GrindstoneBoatEntity>, world: Level): UtilityBoatEntity<BlockEntity, UtilityGrindstoneContainer>(entityType, world) {

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out GrindstoneBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out GrindstoneBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
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
        return ContainerTypes.GrindstoneBoat.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.GrindstoneBoats[boatType]!!.get()
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

class LoomBoatEntity(entityType: EntityType<out LoomBoatEntity>, world: Level): UtilityBoatEntity<BlockEntity, UtilityLoomContainer>(entityType, world) {

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out LoomBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out LoomBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
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
        return ContainerTypes.LoomBoat.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.LoomBoats[boatType]!!.get()
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

class CartographyTableBoatEntity(entityType: EntityType<out CartographyTableBoatEntity>, world: Level): UtilityBoatEntity<BlockEntity, UtilityCartographyTableContainer>(entityType, world) {

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out CartographyTableBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out CartographyTableBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
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
        return ContainerTypes.CartographyTableBoat.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.CartographyTableBoats[boatType]!!.get()
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

class StonecutterBoatEntity(entityType: EntityType<out StonecutterBoatEntity>, world: Level): UtilityBoatEntity<BlockEntity, UtilityStonecutterContainer>(entityType, world) {

    constructor(packet: PlayMessages.SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out StonecutterBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out StonecutterBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
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
        return ContainerTypes.StonecutterBoat.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.StonecutterBoats[boatType]!!.get()
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