package org.jglrxavpok.moarboats.extensions

import net.minecraft.entity.Entity
import net.minecraft.world.World
import java.util.*

fun World.getEntityByUUID(id: UUID): Entity? {
    return this.getLoadedEntityList().find { it.uniqueID == id }
}