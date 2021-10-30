package org.jglrxavpok.moarboats.common.items

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

abstract class MoarBoatsItem(val id: String, propertiesChanger: Properties.() -> Unit = {}, putInItemGroup: Boolean = true):
        Item(Item.Properties()
                .apply{
                    if(putInItemGroup) {
                        this.tab(MoarBoats.MainCreativeTab)
                    }
                }
                .also(propertiesChanger))
{

    init {
        registryName = ResourceLocation(MoarBoats.ModID, id)
    }
}