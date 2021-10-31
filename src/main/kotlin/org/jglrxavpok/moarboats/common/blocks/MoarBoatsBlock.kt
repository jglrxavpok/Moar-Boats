package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.ItemGroup
import org.jglrxavpok.moarboats.MoarBoats

abstract class MoarBoatsBlock(properties: Properties): Block(properties) {

    open val itemGroup: ItemGroup = MoarBoats.MainCreativeTab

    constructor(): this(Properties.of(MoarBoats.MachineMaterial, MaterialColor.METAL).strength(0.5f, 10.5f))

    constructor(propertiesModifier: Properties.() -> Unit):
            this(Properties.of(MoarBoats.MachineMaterial, MaterialColor.METAL).strength(0.5f, 10.5f).also(propertiesModifier))
}