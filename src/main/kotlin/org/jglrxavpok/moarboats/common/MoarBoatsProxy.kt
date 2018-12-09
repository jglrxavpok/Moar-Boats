package org.jglrxavpok.moarboats.common

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.*


open class MoarBoatsProxy {

    open fun init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(MoarBoats, MoarBoatsGuiHandler)
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
                *pluginHandlers.toTypedArray()
                )
    }

    private fun registerMessages(vararg handlers: MBMessageHandler<out IMessage, out IMessage?>) {
        for((packetID, handler) in handlers.withIndex()) {
            handler.registerSelf(MoarBoats.network, packetID)
        }
    }

    open fun preInit() {
    }

    open fun postInit() {
    }

}
