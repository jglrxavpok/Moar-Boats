package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.ItemGroup
import org.jglrxavpok.moarboats.MoarBoats

abstract class MoarBoatsBlock(properties: Block.Properties): Block(properties) {

    open val itemGroup: ItemGroup = MoarBoats.CreativeTab

    constructor(): this(Block.Properties.of(MoarBoats.MachineMaterial, MaterialColor.METAL).strength(0.5f, 10.5f))

    constructor(propertiesModifier: Block.Properties.() -> Unit):
            this(Block.Properties.of(MoarBoats.MachineMaterial, MaterialColor.METAL).strength(0.5f, 10.5f).also(propertiesModifier))
}