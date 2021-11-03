package org.jglrxavpok.moarboats.common.entities.utilityboats

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.MusicDiscItem
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.JukeboxTileEntity
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.JukeboxBoatItem
import org.jglrxavpok.moarboats.common.network.SPlayRecordFromBoat

class JukeboxBoatEntity(world: World): UtilityBoatEntity<JukeboxTileEntity, EmptyContainer>(EntityEntries.JukeboxBoat, world) {

    private var record: ItemStack
        get() = getBackingTileEntity()!!.record
        set(value) {
            getBackingTileEntity()!!.record = value
        }

    private val hasRecord get() = !record.isEmpty
    private val jukeboxPos = BlockPos.Mutable()

    constructor(level: World, x: Double, y: Double, z: Double): this(level) {
        this.setPos(x, y, z)
        this.deltaMovement = Vector3d.ZERO
        this.xOld = x
        this.yOld = y
        this.zOld = z
    }

    override fun initBackingTileEntity(): JukeboxTileEntity? {
        return JukeboxTileEntity()
    }

    override fun tick() {
        jukeboxPos.set(positionX, positionY, positionZ)
        super.tick()
    }

    override fun interact(player: PlayerEntity, hand: Hand): ActionResultType {
        if (super.interact(player, hand) == ActionResultType.SUCCESS)
            return ActionResultType.SUCCESS
        if (world.isClientSide)
            return ActionResultType.SUCCESS

        if(player is ServerPlayerEntity) {
            val heldItem = player.getItemInHand(hand)
            if(heldItem.item is MusicDiscItem && !hasRecord) {
                insertRecord(heldItem.copy())
                if(!player.isCreative) {
                    heldItem.shrink(1)
                }
                return ActionResultType.SUCCESS
            } else {
                if(hasRecord) {
                    ejectRecord()
                }
            }
        }
        return ActionResultType.FAIL
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
        MoarBoats.network.send(PacketDistributor.NEAR.with { PacketDistributor.TargetPoint(positionX, positionY, positionZ, 64.0, world.dimension()) }, SPlayRecordFromBoat(entityID, stack.item as? MusicDiscItem))
    }

    private fun ejectRecord() {
        MoarBoats.network.send(PacketDistributor.NEAR.with { PacketDistributor.TargetPoint(positionX, positionY, positionZ, 64.0, world.dimension()) }, SPlayRecordFromBoat(entityID, null))

        spawnAtLocation(record!!.copy())
        record = ItemStack.EMPTY
    }

    override fun addAdditionalSaveData(compound: CompoundNBT) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("hasRecord", hasRecord)
        if(hasRecord) {
            compound.put("record", record!!.save(CompoundNBT()))
        }
    }

    override fun readAdditionalSaveData(compound: CompoundNBT) {
        super.readAdditionalSaveData(compound)
        val hasRecord = compound.getBoolean("hasRecord")
        record = if(hasRecord) {
            ItemStack.of(compound.getCompound("record"))
        } else {
            ItemStack.EMPTY
        }
    }

    override fun getContainerType(): ContainerType<EmptyContainer> {
        return ContainerTypes.Empty
    }

    override fun getBoatItem(): Item {
        return JukeboxBoatItem[boatType]
    }

    override fun createMenu(p_createMenu_1_: Int, p_createMenu_2_: PlayerInventory, p_createMenu_3_: PlayerEntity): Container? {
        return EmptyContainer(p_createMenu_1_, p_createMenu_2_)
    }
}