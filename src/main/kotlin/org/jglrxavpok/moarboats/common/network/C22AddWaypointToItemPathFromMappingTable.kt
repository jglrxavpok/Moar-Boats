package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class C22AddWaypointToItemPathFromMappingTable: CxxAddWaypointToItemPath {

    constructor()

    var tileEntityX: Int = 0
    var tileEntityY: Int = 0
    var tileEntityZ: Int = 0

    constructor(pos: BlockPos, boost: Double?, mappingTable: TileEntityMappingTable): super(pos, boost) {
        this.tileEntityX = mappingTable.pos.x
        this.tileEntityY = mappingTable.pos.y
        this.tileEntityZ = mappingTable.pos.z
    }

    override fun fromBytes(buf: ByteBuf) {
        super.fromBytes(buf)
        tileEntityX = buf.readInt()
        tileEntityY = buf.readInt()
        tileEntityZ = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        super.toBytes(buf)
        buf.writeInt(tileEntityX)
        buf.writeInt(tileEntityY)
        buf.writeInt(tileEntityZ)
    }

    object Handler: CxxAddWaypointToItemPath.Handler<C22AddWaypointToItemPathFromMappingTable, S24UpdateMapWithPathInMappingTable>() {
        override val item = ItemMapWithPath
        override val packetClass = C22AddWaypointToItemPathFromMappingTable::class

        override fun getStack(message: C22AddWaypointToItemPathFromMappingTable, ctx: MessageContext): ItemStack? {
            with(message) {
                val pos = BlockPos.PooledMutableBlockPos.retain(tileEntityX, tileEntityY, tileEntityZ)
                val te = ctx.serverHandler.player.world.getTileEntity(pos)
                val stack = when(te) {
                    is TileEntityMappingTable -> {
                        te.inventory.getStackInSlot(0)
                    }
                    else -> {
                        MoarBoats.logger.error("Invalid tile entity when trying to add waypoint at $pos")
                        null
                    }
                }
                pos.release()
                return stack
            }
        }

        override fun createResponse(message: C22AddWaypointToItemPathFromMappingTable, ctx: MessageContext, waypointList: NBTTagList): S24UpdateMapWithPathInMappingTable? {
            return S24UpdateMapWithPathInMappingTable(waypointList, message.tileEntityX, message.tileEntityY, message.tileEntityZ)
        }

    }
}