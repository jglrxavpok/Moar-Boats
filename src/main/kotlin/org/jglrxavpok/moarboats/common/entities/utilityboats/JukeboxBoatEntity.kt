package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.RecordItem
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.JukeboxBlockEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.PlayMessages.SpawnEntity
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.network.SPlayRecordFromBoat

class JukeboxBoatEntity(entityType: EntityType<out JukeboxBoatEntity>, world: Level): UtilityBoatEntity<JukeboxBlockEntity, EmptyContainer>(entityType, world) {

    private var record: ItemStack
        get() = getBackingTileEntity()!!.record
        set(value) {
            getBackingTileEntity()!!.record = value
        }

    private val hasRecord get() = !record.isEmpty
    private val jukeboxPos = BlockPos.MutableBlockPos()

    constructor(packet: SpawnEntity, level: Level): this(Registry.ENTITY_TYPE.byId(packet.typeId) as EntityType<out JukeboxBoatEntity>, level, packet.posX, packet.posY, packet.posZ) {}

    constructor(entityType: EntityType<out JukeboxBoatEntity>, level: Level, x: Double, y: Double, z: Double): this(entityType, level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): JukeboxBlockEntity? {
        return JukeboxBlockEntity(InvalidPosition, Blocks.JUKEBOX.defaultBlockState())
    }

    override fun tick() {
        jukeboxPos.set(positionX, positionY, positionZ)
        super.tick()
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        if (super.interact(player, hand) == InteractionResult.SUCCESS)
            return InteractionResult.SUCCESS
        if (world.isClientSide)
            return InteractionResult.SUCCESS

        if(player is ServerPlayer) {
            val heldItem = player.getItemInHand(hand)
            if(heldItem.item is RecordItem && !hasRecord) {
                insertRecord(heldItem.copy())
                if(!player.isCreative) {
                    heldItem.shrink(1)
                }
                return InteractionResult.SUCCESS
            } else {
                if(hasRecord) {
                    ejectRecord()
                }
            }
        }
        return InteractionResult.FAIL
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        dropBaseBoat(killedByPlayerInCreative)
        if(!killedByPlayerInCreative) {
            spawnAtLocation(ItemStack(Items.JUKEBOX))
        }
        if(hasRecord) {
            ejectRecord()
        }
    }

    private fun insertRecord(stack: ItemStack) {
        record = stack
        MoarBoats.network.send(PacketDistributor.NEAR.with { PacketDistributor.TargetPoint(positionX, positionY, positionZ, 64.0, world.dimension()) }, SPlayRecordFromBoat(entityID, stack.item as? RecordItem))
    }

    private fun ejectRecord() {
        MoarBoats.network.send(PacketDistributor.NEAR.with { PacketDistributor.TargetPoint(positionX, positionY, positionZ, 64.0, world.dimension()) }, SPlayRecordFromBoat(entityID, null))

        spawnAtLocation(record!!.copy())
        record = ItemStack.EMPTY
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("hasRecord", hasRecord)
        if(hasRecord) {
            compound.put("record", record!!.save(CompoundTag()))
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        val hasRecord = compound.getBoolean("hasRecord")
        record = if(hasRecord) {
            ItemStack.of(compound.getCompound("record"))
        } else {
            ItemStack.EMPTY
        }
    }

    override fun getContainerType(): MenuType<EmptyContainer> {
        return ContainerTypes.Empty.get()
    }

    override fun getBoatItem(): Item {
        return MBItems.JukeboxBoats[boatType]!!.get()
    }

    override fun createMenu(p_createMenu_1_: Int, p_createMenu_2_: Inventory, p_createMenu_3_: Player): AbstractContainerMenu? {
        return EmptyContainer(p_createMenu_1_, p_createMenu_2_)
    }
}