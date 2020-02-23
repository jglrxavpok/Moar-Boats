package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.math.BlockPos
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import kotlin.reflect.KClass

class CModifyWaypoint(): MoarBoatsPacket {

    private lateinit var waypointData: CompoundNBT
    private var index = 0
    private var teX = 0
    private var teY = 0
    private var teZ = 0

    constructor(te: TileEntityMappingTable, index: Int, waypointData: CompoundNBT): this() {
        this.teX = te.pos.x
        this.teY = te.pos.y
        this.teZ = te.pos.z
        this.index = index
        this.waypointData = waypointData
    }

    object Handler: MBMessageHandler<CModifyWaypoint, MoarBoatsPacket?> {
        override val packetClass = CModifyWaypoint::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CModifyWaypoint, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            with(message) {
                val player = ctx.sender!!
                val level = player.world
                val pos = BlockPos.PooledMutable.retain(teX, teY, teZ)
                val te = level.getTileEntity(pos)
                pos.close()
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