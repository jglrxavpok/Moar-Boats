package org.jglrxavpok.moarboats.common.items

import net.minecraft.world.item.Item
import org.jglrxavpok.moarboats.MoarBoats

abstract class MoarBoatsItem(val id: String, propertiesChanger: Item.Properties.() -> Unit = {}, putInItemGroup: Boolean = true):
        Item(Item.Properties()
                .apply{
                    if(putInItemGroup) {
                        this.tab(MoarBoats.MainCreativeTab)
                    }
                }
                .also(propertiesChanger))
{

}