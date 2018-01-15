package org.jglrxavpok.moarboats.extensions

import net.minecraft.client.gui.MapItemRenderer
import net.minecraft.world.storage.MapData
import java.lang.reflect.Modifier

fun MapItemRenderer.hasMapInstance(name: String) = MapItemRenderer::class.java.declaredFields.any {
    if(it.modifiers and (Modifier.FINAL or Modifier.PRIVATE) != 0 && java.util.Map::class.java.isAssignableFrom(it.type)) {
        it.isAccessible = true
        val value = it.get(this)
        it.isAccessible = false
        value as java.util.Map<*, *>
        return@any value.containsKey(name)
    }
    return@any false
}

fun MapItemRenderer.getMapDataFromName(name: String): MapData? {
    val loadedMapField = MapItemRenderer::class.java.declaredFields.first {
        it.modifiers and (Modifier.FINAL or Modifier.PRIVATE) != 0 && java.util.Map::class.java.isAssignableFrom(it.type)
    }
    loadedMapField.isAccessible = true
    val map = loadedMapField.get(this) as java.util.Map<*, *>
    return map[name] as? MapData
}
