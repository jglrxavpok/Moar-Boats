package org.jglrxavpok.moarboats.common.data

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.network.*
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

interface PathHolder {
    fun pathLoops(): Boolean
    fun setLoopingState(loops: Boolean)
    fun getWaypointNBTList(): NBTTagList
    fun removeWaypoint(closestIndex: Int)
    fun addWaypoint(pos: BlockPos)
    fun getHolderLocation(): BlockPos?
    fun sendWorldImageRequest(mapID: String)
    fun getBaseMapID(): String
}

class BoatPathHolder(val boat: IControllable): PathHolder {
    override fun getBaseMapID(): String {
        return HelmModule.mapDataCopyProperty[boat].mapName
    }

    override fun sendWorldImageRequest(mapID: String) {
        MoarBoats.network.sendToServer(C10MapImageRequest(mapID))
    }

    override fun addWaypoint(pos: BlockPos) {
        MoarBoats.network.sendToServer(C12AddWaypoint(pos, boat.entityID))
    }

    override fun removeWaypoint(closestIndex: Int) {
        MoarBoats.network.sendToServer(C13RemoveWaypoint(closestIndex, boat.entityID))
    }

    override fun getWaypointNBTList(): NBTTagList {
        return boat.getState(HelmModule).getTagList(HelmModule.waypointsProperty.id, Constants.NBT.TAG_COMPOUND)
    }

    override fun pathLoops(): Boolean {
        return HelmModule.loopingProperty[boat]
    }

    override fun setLoopingState(loops: Boolean) {
        MoarBoats.network.sendToServer(C14ChangeLoopingState(loops, boat.entityID))
    }

    override fun getHolderLocation(): BlockPos {
        return boat.blockPos
    }
}

class MapWithPathHolder(val stack: ItemStack, val mappingTable: TileEntityMappingTable?, val boat: IControllable?): PathHolder {

    private fun nbt(): NBTTagCompound {
        if(stack.tagCompound == null) {
            stack.tagCompound = NBTTagCompound()
        }
        return stack.tagCompound!!
    }

    override fun getBaseMapID(): String {
        return nbt().getString("${MoarBoats.ModID}.mapID")
    }

    override fun pathLoops(): Boolean {
        return nbt().getBoolean("${MoarBoats.ModID}.loops")
    }

    override fun setLoopingState(loops: Boolean) {
        nbt().setBoolean("${MoarBoats.ModID}.loops", loops)
    }

    override fun getWaypointNBTList(): NBTTagList {
        return nbt().getTagList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
    }

    override fun getHolderLocation() = null

    override fun addWaypoint(pos: BlockPos) {
        if(mappingTable != null) {
            MoarBoats.network.sendToServer(C22AddWaypointToItemPathFromMappingTable(pos, mappingTable))
        } else if(boat != null) {
            MoarBoats.network.sendToServer(C23AddWaypointToItemPathFromBoat(pos, boat.entityID))
        }
    }

    override fun removeWaypoint(closestIndex: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendWorldImageRequest(mapID: String) {
        MoarBoats.network.sendToServer(C10MapImageRequest(mapID))
    }

}