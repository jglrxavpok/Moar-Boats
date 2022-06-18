package org.jglrxavpok.moarboats.common

import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.*

object Items {

    val Registry = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            MoarBoats.ModID
    )

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
            *StonecutterBoatItem.AllVersions,
            *ChestBoatItem.AllVersions,
            *EnderChestBoatItem.AllVersions,
            *ShulkerBoatItem.AllVersions,
            *JukeboxBoatItem.AllVersions

    )

}