package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

object BlockBoatBattery: Block(Material.IRON) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_battery")
        unlocalizedName = "boat_battery"
        setCreativeTab(MoarBoats.CreativeTab)
    }
}