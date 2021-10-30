package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class SUpdateMapWithPathInMappingTable: SxxUpdateMapWithPath {

    var tileEntityX: Int = 0
    var tileEntityY: Int = 0
    var tileEntityZ: Int = 0

    constructor()

    constructor(list: ListNBT, tileEntityX: Int, tileEntityY: Int, tileEntityZ: Int): super(list) {
        this.tileEntityX = tileEntityX
        this.tileEntityY = tileEntityY
        this.tileEntityZ = tileEntityZ
    }

    object Handler: SxxUpdateMapWithPath.Handler<SUpdateMapWithPathInMappingTable>() {
        override val packetClass = SUpdateMapWithPathInMappingTable::class.java

        override fun updatePath(message: SUpdateMapWithPathInMappingTable, ctx: NetworkEvent.Context, list: ListNBT) {
            with(message) {
                val pos = BlockPos.Mutable(tileEntityX, tileEntityY, tileEntityZ)
                val te = Minecraft.getInstance().level!!.getBlockEntity(pos)
                when(te) {
                    is TileEntityMappingTable -> {
                        val stack = te.inventory.getItem(0)
                        if(stack.tag == null) {
                            stack.tag = CompoundNBT()
                        }
                        stack.tag!!.put("${MoarBoats.ModID}.path", list)
                    }
                    else -> MoarBoats.logger.error("No mapping table at $pos")
                }
            }
        }

    }
}