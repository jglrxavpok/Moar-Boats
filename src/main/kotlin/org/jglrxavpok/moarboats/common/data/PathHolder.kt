package org.jglrxavpok.moarboats.common.data

import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.util.Constants
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.network.C10MapImageRequest
import org.jglrxavpok.moarboats.common.network.C12AddWaypoint
import org.jglrxavpok.moarboats.common.network.C13RemoveWaypoint
import org.jglrxavpok.moarboats.common.network.C14ChangeLoopingState

interface PathHolder {
    fun pathLoops(): Boolean
    fun setLoopingState(loops: Boolean)
    fun getWaypointNBTList(): NBTTagList
    fun removeWaypoint(closestIndex: Int)
    fun addWaypoint(pos: BlockPos)
    fun getHolderLocation(): BlockPos
    fun sendWorldImageRequest(mapID: String)
}

class BoatPathHolder(val boat: IControllable): PathHolder {
    override fun sendWorldImageRequest(mapID: String) {
        MoarBoats.network.sendToServer(C10MapImageRequest(mapID, boat.entityID, HelmModule.id))
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