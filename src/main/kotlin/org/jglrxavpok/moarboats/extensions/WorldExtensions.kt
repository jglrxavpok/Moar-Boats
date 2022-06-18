package org.jglrxavpok.moarboats.extensions

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import java.util.*
import java.util.function.Predicate

fun Level.getEntityByUUID(id: UUID): Entity? {
    return this.getEntities<Entity>(null) {true}.find { it.uuid == id }
}

inline fun <reified T: Entity> Level.getEntities(type: EntityType<T>?, crossinline predicate: (T) -> Boolean): List<Entity> {
    return if(this is ServerLevel) {
        this.getEntities(type, Predicate { ent: Entity ->
            if(ent is T) {
                predicate(ent as T)
            } else {
                false
            }
        })
    } else {
        (this as ClientLevel).entitiesForRendering().filter { it ->
            if(it is T) {
                (type == null || it.type == type) && predicate(it as T)
            } else {
                false
            }
        }
    }
}