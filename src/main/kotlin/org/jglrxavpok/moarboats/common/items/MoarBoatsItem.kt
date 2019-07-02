package org.jglrxavpok.moarboats.common.items

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

abstract class MoarBoatsItem(val id: String): Item(Item.Properties().group(MoarBoats.CreativeTab)) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "icebreaker")
    }
}