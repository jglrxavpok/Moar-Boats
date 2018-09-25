package org.jglrxavpok.moarboats.common

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import org.jglrxavpok.moarboats.common.items.*
import net.minecraft.init.Items as MCItems

object Items {

    /**
     * The order of this list determines the order in which items appear in the creative tab
     */
    val list = listOf(
            ModularBoatItem,
            AnimalBoatItem,
            SeatItem,
            HelmItem,
            RudderItem,
            IceBreakerItem,
            RopeItem,
            DivingBottleItem,
            WaterborneConductorItem,
            WaterborneComparatorItem,
            CreativeEngineItem//,
           // ItemGoldenItinerary,
           // ItemMapWithPath
    )

}