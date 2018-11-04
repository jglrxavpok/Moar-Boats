package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import org.jglrxavpok.moarboats.extensions.swap

class CSwapWaypoints(): IMessage {

    private var index1 = -1
    private var index2 = -1
    private var x = -1
    private var y = -1
    private var z = -1

    constructor(index1: Int, index2: Int, pos: BlockPos): this() {
        this.index1 = index1
        this.index2 = index2
        this.x = pos.x
        this.y = pos.y
        this.z = pos.z
    }

    override fun fromBytes(buf: ByteBuf) {
        index1 = buf.readInt()
        index2 = buf.readInt()
        x = buf.readInt()
        y = buf.readInt()
        z = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(index1)
        buf.writeInt(index2)
        buf.writeInt(x)
        buf.writeInt(y)
        buf.writeInt(z)
    }

    object Handler: MBMessageHandler<CSwapWaypoints, SConfirmWaypointSwap?> {
        override val packetClass = CSwapWaypoints::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CSwapWaypoints, ctx: MessageContext): SConfirmWaypointSwap? {
            with(message) {
                val player = ctx.serverHandler.player
                val world = player.world
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.release()
                return when(te) {
                    is TileEntityMappingTable -> {
                        val stack = te.inventory.getStackInSlot(0)
                        val item = stack.item as ItemPath
                        item.getWaypointData(stack, MoarBoats.getLocalMapStorage()).swap(index1, index2)
                        SConfirmWaypointSwap()
                    }
                    else -> null
                }
            }
        }

    }
}