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

inline fun <reified T: Entity> World.getEntities(type: EntityType<T>?, crossinline predicate: (T) -> Boolean): List<Entity> {
    return if(this is ServerWorld) {
        this.getEntities(type, Predicate { ent: Entity ->
            if(ent is T) {
                predicate(ent as T)
            } else {
                false
            }
        })
    } else {
        (this as ClientWorld).allEntities.filter { it ->
            if(it is T) {
                (type == null || it.type == type) && predicate(it as T)
            } else {
                false
            }
        }
    }
}

fun <T> BlockPos.PooledMutable.use(action: (BlockPos.PooledMutable) -> T): T {
    val result = action(this)
    this.close()
    return result
}