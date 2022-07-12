package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.ListTag
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable

abstract class SxxUpdateMapWithPath: ServerMoarBoatsPacket {

    constructor(openEditMenuOfMappingTable: Boolean) {
        this.openEditMenuOfMappingTable = openEditMenuOfMappingTable
    }

    lateinit var list: ListTag

    @MoarBoatsPacket.Ignore
    val openEditMenuOfMappingTable: Boolean

    constructor(waypointList: ListTag, openEditMenuOfMappingTable: Boolean) {
        this.list = waypointList
        this.openEditMenuOfMappingTable = openEditMenuOfMappingTable
    }

    abstract class Handler<T: SxxUpdateMapWithPath>: MBMessageHandler<T, MoarBoatsPacket?> {
        abstract fun updatePath(message: T, ctx: NetworkEvent.Context, list: ListTag)
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: T, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val list = message.list
            updatePath(message, ctx, list)

            if(message.openEditMenuOfMappingTable && Minecraft.getInstance().screen is GuiMappingTable) {
                val mappingTable = Minecraft.getInstance().screen as GuiMappingTable
                mappingTable.confirmWaypointCreation(list)
            }
            return null
        }
    }


}