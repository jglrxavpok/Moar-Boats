package org.jglrxavpok.moarboats.common.data

import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.core.BlockPos
import net.minecraft.nbt.Tag
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.network.*
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

interface PathHolder {
    fun getLoopingOption(): LoopingOptions
    fun setLoopingState(loopingOptions: LoopingOptions)
    fun getWaypointNBTList(): ListTag
    fun removeWaypoint(closestIndex: Int)
    fun addWaypoint(pos: BlockPos, boost: Double?)
    fun getHolderLocation(): BlockPos?
    fun sendWorldImageRequest(mapID: String)
}

class BoatPathHolder(val boat: IControllable): PathHolder {

    override fun sendWorldImageRequest(mapID: String) {
        MoarBoats.network.sendToServer(CMapImageRequest(mapID))
    }

    override fun addWaypoint(pos: BlockPos, boost: Double?) {
        MoarBoats.network.sendToServer(CAddWaypoint(pos, boat.entityID, boost))
    }

    override fun removeWaypoint(closestIndex: Int) {
        MoarBoats.network.sendToServer(CRemoveWaypoint(closestIndex, boat.entityID))
    }

    override fun getWaypointNBTList(): ListTag {
        return boat.getState(HelmModule).getList(HelmModule.waypointsProperty.id, Tag.TAG_COMPOUND.toInt())
    }

    override fun getLoopingOption(): LoopingOptions {
        return HelmModule.loopingProperty[boat]
    }

    override fun setLoopingState(loopingOptions: LoopingOptions) {
        MoarBoats.network.sendToServer(CChangeLoopingState(loopingOptions, boat.entityID))
    }

    override fun getHolderLocation(): BlockPos {
        return BlockPos(boat.positionX, boat.positionY, boat.positionZ)
    }
}

class MapWithPathHolder(stack: ItemStack, mappingTable: TileEntityMappingTable?, boat: IControllable?): ItemPathHolder(stack, mappingTable, boat) {

    override fun nbt(): CompoundTag {
        if(stack.tag == null) {
            stack.tag = CompoundTag()
        }
        return stack.tag!!
    }

    override fun addWaypoint(pos: BlockPos, boost: Double?) {
        if(mappingTable != null) {
            MoarBoats.network.sendToServer(CAddWaypointToItemPathFromMappingTable(pos, boost, null, mappingTable))
        } else if(boat != null) {
            MoarBoats.network.sendToServer(CAddWaypointToItemPathFromBoat(pos, boost, null, boat.entityID))
        }
    }

    override fun removeWaypoint(closestIndex: Int) {
        if(mappingTable != null) {
            MoarBoats.network.sendToServer(CRemoveWaypointFromMapWithPathFromMappingTable(closestIndex, mappingTable))
        } else if(boat != null) {
            MoarBoats.network.sendToServer(CRemoveWaypointFromMapWithPathFromBoat(closestIndex, boat.entityID))
        }
    }
}

class GoldenTicketPathHolder(stack: ItemStack, mappingTable: TileEntityMappingTable?, boat: IControllable?): ItemPathHolder(stack, mappingTable, boat) {
    override fun nbt(): CompoundTag {
        return ItemGoldenTicket.getData(stack).save(CompoundTag())
    }

    override fun addWaypoint(pos: BlockPos, boost: Double?) {
        if(mappingTable != null) {
            MoarBoats.network.sendToServer(CAddWaypointToGoldenTicketFromMappingTable(pos, boost, null, mappingTable))
        } else if(boat != null) {
            MoarBoats.network.sendToServer(CAddWaypointToGoldenTicketFromBoat(pos, boost, null, boat.entityID))
        }
    }

    override fun removeWaypoint(closestIndex: Int) {
        if(mappingTable != null) {
            MoarBoats.network.sendToServer(CRemoveWaypointFromGoldenTicketFromMappingTable(closestIndex, mappingTable))
        } else if(boat != null) {
            MoarBoats.network.sendToServer(CRemoveWaypointFromGoldenTicketFromBoat(closestIndex, boat.entityID))
        }
    }
}

abstract class ItemPathHolder(val stack: ItemStack, val mappingTable: TileEntityMappingTable?, val boat: IControllable?): PathHolder {

    abstract fun nbt(): CompoundTag

    override fun getLoopingOption(): LoopingOptions {
        var loopingOption = LoopingOptions.NoLoop
        if(nbt().getBoolean("${MoarBoats.ModID}.loops")) { // retro-compatibility
            loopingOption = LoopingOptions.Loops
        }
        if(nbt().contains("${MoarBoats.ModID}.loopingOption")) {
            loopingOption = LoopingOptions.values()[nbt().getInt("${MoarBoats.ModID}.loopingOption").coerceIn(LoopingOptions.values().indices)]
        }
        return loopingOption
    }

    override fun setLoopingState(loopingOptions: LoopingOptions) {
        if(boat != null) {
            MoarBoats.network.sendToServer(CChangeLoopingStateItemPathBoat(loopingOptions, boat.entityID))
        } else {
            MoarBoats.network.sendToServer(CChangeLoopingStateItemPathMappingTable(loopingOptions, mappingTable!!))
        }
    }

    override fun getWaypointNBTList(): ListTag {
        return nbt().getList("${MoarBoats.ModID}.path", Tag.TAG_COMPOUND.toInt())
    }

    override fun getHolderLocation() = null

    override fun sendWorldImageRequest(mapID: String) {
        MoarBoats.network.sendToServer(CMapImageRequest(mapID))
    }

}