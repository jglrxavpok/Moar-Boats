package org.jglrxavpok.moarboats.common.items

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

abstract class MoarBoatsItem(val id: String, propertiesChanger: Properties.() -> Unit = {}): Item(Item.Properties().group(MoarBoats.CreativeTab).also(propertiesChanger)) {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, id)
    }
}