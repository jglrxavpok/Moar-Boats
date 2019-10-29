package org.jglrxavpok.moarboats.extensions

import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import java.util.*
import java.util.function.Predicate

fun World.getEntityByUUID(id: UUID): Entity? {
    return this.getEntities<Entity>(null) {true}.find { it.uniqueID == id }
}

fun <T: Entity> World.getEntities(type: EntityType<T>?, predicate: (T) -> Boolean): List<Entity> {
    return if(this is ServerWorld) {
        this.getEntities(type, Predicate { ent: Entity -> predicate(ent as T) })
    } else {
        (this as ClientWorld).allEntities.filter { it -> (type == null || it.type == type) && predicate(it as T) }
    }
}

fun <T> BlockPos.PooledMutableBlockPos.use(action: (BlockPos.PooledMutableBlockPos) -> T): T {
    val result = action(this)
    this.close()
    return result
}