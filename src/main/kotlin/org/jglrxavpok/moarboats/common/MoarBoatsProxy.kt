package org.jglrxavpok.moarboats.common

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.*


open class MoarBoatsProxy {
    open fun init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(MoarBoats, MoarBoatsGuiHandler)
        registerMessages(
                C0OpenModuleGui.Handler,
                C2MapRequest.Handler,
                S3MapAnswer.Handler,
                C4ChangeEngineMode.Handler,
                C5DeployAnchor.Handler,
                S6PlaySound.Handler,
                S7SyncInventory.Handler,
                C8ChangeEngineSpeed.Handler,
                C9ChangeDispenserPeriod.Handler,
                C10MapImageRequest.Handler,
                S11MapImageAnswer.Handler,
                C12AddWaypoint.Handler,
                C13RemoveWaypoint.Handler,
                C14ChangeLoopingState.Handler,
                S15ModuleData.Handler,
                S16ModuleLocations.Handler,
                C17RemoveModule.Handler,
                C18ChangeDispenserFacing.Handler,
                S19UpdateFluidGui.Handler,
                C20SaveItineraryToMap.Handler,
                S21SetGoldenItinerary.Handler,
                C22AddWaypointToItemPathFromMappingTable.Handler,
                C23AddWaypointToItemPathFromBoat.Handler,
                S24UpdateMapWithPathInMappingTable.Handler,
                S25UpdateMapWithPathInBoat.Handler,
                C26RemoveWaypointFromMapWithPathFromMappingTable.Handler,
                C27RemoveWaypointFromMapWithPathFromBoat.Handler,
                C28RemoveWaypointFromGoldenTicketFromMappingTable.Handler,
                C29RemoveWaypointFromGoldenTicketFromBoat.Handler,
                C30AddWaypointToGoldenTicketFromMappingTable.Handler,
                C31AddWaypointToGoldenTicketFromBoat.Handler,
                SConfirmWaypointCreation.Handler,
                CModifyWaypoint.Handler,
                CChangeLoopingStateItemPathBoat.Handler,
                CChangeLoopingStateItemPathMappingTable.Handler,
                CSwapWaypoints.Handler,
                SConfirmWaypointSwap.Handler
                )
    }

    private fun registerMessages(vararg handlers: MBMessageHandler<out IMessage, out IMessage?>) {
        for((packetID, handler) in handlers.withIndex()) {
            handler.registerSelf(MoarBoats.network, packetID)
        }
    }

    open fun preInit() {
    }

}
