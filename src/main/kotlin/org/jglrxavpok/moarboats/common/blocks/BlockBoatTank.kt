package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

object BlockBoatTank: Block(Material.IRON) {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "boat_tank")
        unlocalizedName = "boat_tank"
        setCreativeTab(MoarBoats.CreativeTab)
    }
}