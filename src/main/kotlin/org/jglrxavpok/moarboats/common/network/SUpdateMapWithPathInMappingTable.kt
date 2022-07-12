package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.core.BlockPos
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class SUpdateMapWithPathInMappingTable: SxxUpdateMapWithPath {

    var tileEntityX: Int = 0
    var tileEntityY: Int = 0
    var tileEntityZ: Int = 0

    constructor(): super(true)

    constructor(list: ListTag, tileEntityX: Int, tileEntityY: Int, tileEntityZ: Int): super(list, true) {
        this.tileEntityX = tileEntityX
        this.tileEntityY = tileEntityY
        this.tileEntityZ = tileEntityZ
    }

    object Handler: SxxUpdateMapWithPath.Handler<SUpdateMapWithPathInMappingTable>() {
        override val packetClass = SUpdateMapWithPathInMappingTable::class.java

        override fun updatePath(message: SUpdateMapWithPathInMappingTable, ctx: NetworkEvent.Context, list: ListTag) {
            with(message) {
                val pos = BlockPos.MutableBlockPos(tileEntityX, tileEntityY, tileEntityZ)
                val te = Minecraft.getInstance().level!!.getBlockEntity(pos)
                when(te) {
                    is TileEntityMappingTable -> {
                        val stack = te.inventory.getItem(0).copy()
                        if(stack.tag == null) {
                            stack.tag = CompoundTag()
                        }
                        stack.tag!!.put("${MoarBoats.ModID}.path", list)
                        te.inventory.setItem(0, stack)
                    }
                    else -> MoarBoats.logger.error("No mapping table at $pos")
                }
            }
        }

    }
}