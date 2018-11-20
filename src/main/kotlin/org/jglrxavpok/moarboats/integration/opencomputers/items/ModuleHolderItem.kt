package org.jglrxavpok.moarboats.integration.opencomputers.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.OnlyUsableOnBoats
import org.jglrxavpok.moarboats.common.items.ChunkLoaderItem

object ModuleHolderItem: Item() {
    init {
        creativeTab = MoarBoats.CreativeTab
        unlocalizedName = "opencomputers_holder"
        registryName = ResourceLocation(MoarBoats.ModID, "opencomputers_holder")
        maxStackSize = 1
    }


}