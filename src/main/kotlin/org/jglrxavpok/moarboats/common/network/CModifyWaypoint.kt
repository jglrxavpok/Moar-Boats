package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import kotlin.reflect.KClass

class CModifyWaypoint(): IMessage {

    private lateinit var waypointData: NBTTagCompound
    private var index = 0
    private var teX = 0
    private var teY = 0
    private var teZ = 0

    constructor(te: TileEntityMappingTable, index: Int, waypointData: NBTTagCompound): this() {
        this.teX = te.pos.x
        this.teY = te.pos.y
        this.teZ = te.pos.z
        this.index = index
        this.waypointData = waypointData
    }

    override fun fromBytes(buf: ByteBuf) {
        teX = buf.readInt()
        teY = buf.readInt()
        teZ = buf.readInt()
        index = buf.readInt()
        waypointData = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(teX)
        buf.writeInt(teY)
        buf.writeInt(teZ)
        buf.writeInt(index)
        ByteBufUtils.writeTag(buf, waypointData)
    }

    object Handler: MBMessageHandler<CModifyWaypoint, IMessage?> {
        override val packetClass = CModifyWaypoint::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CModifyWaypoint, ctx: MessageContext): IMessage? {
            with(message) {
                val player = ctx.serverHandler.player
                val world = player.world
                val pos = BlockPos.PooledMutableBlockPos.retain(teX, teY, teZ)
                val te = world.getTileEntity(pos)
                pos.release()
                when(te) {
                    is TileEntityMappingTable -> {
                        val stack = te.inventory.getStackInSlot(0)
                        val item = stack.item as ItemPath
                        item.getWaypointData(stack, MoarBoats.getLocalMapStorage()).set(index, waypointData)
                    }
                    else -> null
                }
            }
            return null
        }

    }
}