package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import kotlin.reflect.KClass

class CChangeLoopingStateItemPathMappingTable: CChangeLoopingStateBase {

    constructor(): super()

    var teX: Int = -1
    var teY: Int = -1
    var teZ: Int = -1

    constructor(loops: Boolean, mappingTable: TileEntityMappingTable): super(loops) {
        this.teX = mappingTable.pos.x
        this.teY = mappingTable.pos.y
        this.teZ = mappingTable.pos.z
    }

    override fun fromBytes(buf: ByteBuf) {
        super.fromBytes(buf)
        teX = buf.readInt()
        teY = buf.readInt()
        teZ = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        super.toBytes(buf)
        buf.writeInt(teX)
        buf.writeInt(teY)
        buf.writeInt(teZ)
    }

    object Handler: MBMessageHandler<CChangeLoopingStateItemPathMappingTable, IMessage?> {
        override val packetClass = CChangeLoopingStateItemPathMappingTable::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: CChangeLoopingStateItemPathMappingTable, ctx: MessageContext): IMessage? {
            with(message) {
                val player = ctx.serverHandler.player
                val world = player.world
                val pos = BlockPos.PooledMutableBlockPos.retain(teX, teY, teZ)
                val te = world.getTileEntity(pos)
                pos.release()
                if(te !is TileEntityMappingTable)
                    return null
                val stack = te.inventory.getStackInSlot(0)
                val item = stack.item
                if(item is ItemPath) {
                    item.setLooping(stack, message.loops)
                    when(item) {
                        is ItemGoldenTicket -> return S21SetGoldenItinerary(item.getData(stack))
                        is ItemMapWithPath -> return S24UpdateMapWithPathInMappingTable(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), teX, teY, teZ)
                    }
                }
                return null
            }
        }

    }
}