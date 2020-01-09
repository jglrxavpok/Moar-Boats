package org.jglrxavpok.moarboats.common

import org.jglrxavpok.moarboats.common.items.*
import net.minecraft.item.Items as MCItems

object Items {

    /**
     * The order of this list determines the order in which items appear in the creative tab
     */
    val list = listOf(
            *ModularBoatItem.AllVersions,
            AnimalBoatItem,
            SeatItem,
            HelmItem,
            RudderItem,
            IceBreakerItem,
            RopeItem,
            DivingBottleItem,
            WaterborneConductorItem,
            WaterborneComparatorItem,
            CreativeEngineItem,
            ItemGoldenTicket,
            MapItemWithPath,
            ChunkLoaderItem,
            OarsItem,
            CargoStopperItem,

            *FurnaceBoatItem.AllVersions,
            *SmokerBoatItem.AllVersions,
            *BlastFurnaceBoatItem.AllVersions,
            *CraftingTableBoatItem.AllVersions,
            *GrindstoneBoatItem.AllVersions,
            *LoomBoatItem.AllVersions,
            *CartographyTableBoatItem.AllVersions,
            *ChestBoatItem.AllVersions,
            *EnderChestBoatItem.AllVersions,
            *ShulkerBoatItem.AllVersions,
            *JukeboxBoatItem.AllVersions

    )

}