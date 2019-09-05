package org.jglrxavpok.moarboats.extensions

import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

fun level.getEntityByUUID(id: UUID): Entity? {
    return this.loadedEntityList.find { it.uniqueID == id }
}


fun <T> BlockPos.PooledMutableBlockPos.use(action: (BlockPos.PooledMutableBlockPos) -> T): T {
    val result = action(this)
    this.close()
    return result
}