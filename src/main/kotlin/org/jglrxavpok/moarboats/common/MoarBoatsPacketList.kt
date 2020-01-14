package org.jglrxavpok.moarboats.common

import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.*


object MoarBoatsPacketList {

    fun registerAll() {
        val pluginHandlers = MoarBoats.plugins.flatMap { it.handlers() }
        registerMessages(
                COpenModuleGui.Handler,
                CMapRequest.Handler,
                SMapAnswer.Handler,
                CChangeEngineMode.Handler,
                CDeployAnchor.Handler,
                SPlaySound.Handler,
                SSyncInventory.Handler,
                CChangeEngineSpeed.Handler,
                CChangeDispenserPeriod.Handler,
                CMapImageRequest.Handler,
                SMapImageAnswer.Handler,
                CAddWaypoint.Handler,
                CRemoveWaypoint.Handler,
                CChangeLoopingState.Handler,
                SModuleData.Handler,
                SModuleLocations.Handler,
                CRemoveModule.Handler,
                CChangeDispenserFacing.Handler,
                SUpdateFluidGui.Handler,
                CSaveItineraryToMap.Handler,
                SSetGoldenItinerary.Handler,
                CAddWaypointToItemPathFromMappingTable.Handler,
                CAddWaypointToItemPathFromBoat.Handler,
                SUpdateMapWithPathInMappingTable.Handler,
                SUpdateMapWithPathInBoat.Handler,
                CRemoveWaypointFromMapWithPathFromMappingTable.Handler,
                CRemoveWaypointFromMapWithPathFromBoat.Handler,
                CRemoveWaypointFromGoldenTicketFromMappingTable.Handler,
                CRemoveWaypointFromGoldenTicketFromBoat.Handler,
                CAddWaypointToGoldenTicketFromMappingTable.Handler,
                CAddWaypointToGoldenTicketFromBoat.Handler,
                SConfirmWaypointCreation.Handler,
                CModifyWaypoint.Handler,
                CChangeLoopingStateItemPathBoat.Handler,
                CChangeLoopingStateItemPathMappingTable.Handler,
                CSwapWaypoints.Handler,
                SConfirmWaypointSwap.Handler,
                CChangeRudderBlocking.Handler,
                SUtilityTileEntityUpdate.Handler,
                SPlayRecordFromBoat.Handler,
                CShowBoatMenu.Handler,
                *pluginHandlers.toTypedArray()
                )
    }

    private fun registerMessages(vararg handlers: MBMessageHandler<out MoarBoatsPacket, out MoarBoatsPacket?>) {
        for((packetID, handler) in handlers.withIndex()) {
            handler.registerSelf(MoarBoats.network, packetID)
        }
    }

}
