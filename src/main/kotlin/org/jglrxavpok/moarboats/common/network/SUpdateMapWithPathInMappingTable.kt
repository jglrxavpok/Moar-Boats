package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class SUpdateMapWithPathInMappingTable: SxxUpdateMapWithPath {

    var tileEntityX: Int = 0
    var tileEntityY: Int = 0
    var tileEntityZ: Int = 0

    constructor()

    constructor(list: NBTTagList, tileEntityX: Int, tileEntityY: Int, tileEntityZ: Int): super(list) {
        this.tileEntityX = tileEntityX
        this.tileEntityY = tileEntityY
        this.tileEntityZ = tileEntityZ
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

    object Handler: SxxUpdateMapWithPath.Handler<SUpdateMapWithPathInMappingTable>() {
        override val packetClass = SUpdateMapWithPathInMappingTable::class

        override fun updatePath(message: SUpdateMapWithPathInMappingTable, ctx: MessageContext, list: NBTTagList) {
            with(message) {
                val pos = BlockPos.PooledMutableBlockPos.retain(tileEntityX, tileEntityY, tileEntityZ)
                val te = Minecraft.getMinecraft().world.getTileEntity(pos)
                when(te) {
                    is TileEntityMappingTable -> {
                        val stack = te.inventory.getStackInSlot(0)
                        if(stack.tagCompound == null) {
                            stack.tagCompound = NBTTagCompound()
                        }
                        stack.tagCompound!!.setTag("${MoarBoats.ModID}.path", list)
                    }
                    else -> MoarBoats.logger.error("No mapping table at $pos")
                }
                pos.release()
            }
        }

    }
}